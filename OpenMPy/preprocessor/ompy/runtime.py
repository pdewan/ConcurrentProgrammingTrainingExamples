from threading import Thread, Lock, current_thread
from time import time
from math import floor
#from jarray import array
from collections import deque
from time import sleep
from operator import xor


class RuntimeManager:
    def __init__(self, num_threads):
        self.for_manager = None
        self.num_threads = num_threads
        self.barrier_lock = Lock()
        self.barrier_count = 0
        self.barrier_done = False
        self.critical_lock = Lock()
        self.atomic_lock = Lock()
        self.reduction_lock = Lock()
        self.reductions = {}

    def set_for(self, for_manager):
        self.for_manager = for_manager

    def get_for(self):
        while self.for_manager is None:
            continue
        return self.for_manager

    def barrier(self):
        self.barrier_lock.acquire()
        if self.barrier_count == 0:
            self.barrier_done = False
        self.barrier_count += 1
        if self.barrier_count == get_num_threads():
            self.barrier_done = True
            self.barrier_count = 0
        self.barrier_lock.release()
        while not self.barrier_done:
            continue

    def update_reduction_variable(self, name, val, op):
        self.reduction_lock.acquire()
        if name in self.reductions:
            self.reductions[name].append(val)
        else:
            self.reductions[name] = [op, val]
        self.reduction_lock.release()


    def get_reduction_value(self, name):
        op = self.reductions[name][0]
        if op == '+' or op == '-':
            result = 0
            for i in range(len(self.reductions[name]) - 1):
                result += self.reductions[name][i + 1]
            return result

        if op == '*':
            result = 1
            for i in range(len(self.reductions[name]) - 1):
                result *= self.reductions[name][i + 1]
            return result

        elif op == '&&':
            result = bool(self.reductions[name][1] and self.reductions[name][2])
            for i in range(len(self.reductions[name]) - 3):
                result = bool(result and self.reductions[name][i + 3])
            return result

        elif op == '||':
            result = bool(self.reductions[name][1] or self.reductions[name][2])
            for i in range(len(self.reductions[name]) - 3):
                result = bool(result or self.reductions[name][i + 3])
            return result

        elif op == '&':
            #print '&: '
            result = self.reductions[name][1] & self.reductions[name][2]
            #print bin(self.reductions[name][1]), '\n', bin(self.reductions[name][2])
            for i in range(len(self.reductions[name]) - 3):
                #print bin(self.reductions[name][i + 3])
                result = result & self.reductions[name][i + 3]
            return result

        elif op == '|':
            result = self.reductions[name][1] | self.reductions[name][2]
            #print bin(self.reductions[name][1]), '\n', bin(self.reductions[name][2])
            for i in range(len(self.reductions[name]) - 3):
                #print bin(self.reductions[name][i + 3])
                result = result | self.reductions[name][i + 3]
            return result

        elif op == '^':
            result = xor(self.reductions[name][1], self.reductions[name][2])
            #print bin(self.reductions[name][1]), '\n', bin(self.reductions[name][2])
            for i in range(len(self.reductions[name]) - 3):
                #print bin(self.reductions[name][i + 3])
                result = xor(result, self.reductions[name][i + 3])
            return result

        elif op == 'max':
            return max(self.reductions[name][1:])

        elif op == 'min':
            return min(self.reductions[name][1:])


def get_num_threads():
    try:
        return current_thread().get_num_threads()

    except AttributeError:
        # this can only happen if this is called from the mainthread, implying only
        # one thread is running. This shouldn't happen in practice
        return 1

def get_current_thread_id():
    if current_thread().name == 'MainThread':
        return 0
    else:
        return current_thread().get_id()

class OmpThread(Thread):
    def __init__(self, my_id, target=None, num_threads=1, args=None):
        super(OmpThread, self).__init__(name=str(my_id), target=target, args=args)
        self.my_id = my_id
        self.num_threads = num_threads

    def get_id(self):
        return self.my_id

    def get_num_threads(self):
        return self.num_threads


def submit(fun, num_threads, args=None):
    if args is None:
        threads = [OmpThread(i, target=fun, num_threads=num_threads, args=()) for i in range(num_threads)]
    else:
        threads = [OmpThread(i, target=fun, num_threads=num_threads, args=args) for i in range(num_threads)]
    for thread in threads:
        thread.start()
    for thread in threads:
        thread.join()


class ForManager:
    def __init__(self, schedule, chunk, num_threads):
        self.lock = Lock()

        self.start = None
        self.end = None
        self.step = None
        self.count_up = True
        self.setup_complete = False
        self.total_iterations_remaining = None
        self.num_threads = num_threads
        self.chunk = chunk
        self.schedule = schedule if schedule else 'static'
        #self.static_iterations_stacks = [[] for x in range(num_threads)]
        self.static_iterations_stacks = [deque() for x in range(num_threads)]

    def setup(self, arg1, arg2, arg3):
        if get_current_thread_id() == 0:
            self.start = arg1 if arg2 is not None else 0
            self.end = arg2 if arg2 is not None else arg1
            self.step = arg3 if arg3 is not None else 1
            self.total_iterations_remaining = int((self.end - self.start) / self.step)

            if self.total_iterations_remaining < 0:
                if self.step >= 0:
                    raise Exception('invalid for loop arguments [start: {}, end: {}, step: {}'.format(self.start, self.end, self.step))
                self.count_up = False
                self.total_iterations_remaining *= -1

            if self.total_iterations_remaining == 0:
                raise Exception('invalid for loop arguments [start: {}, end: {}, step: {}'.format(self.start, self.end, self.step))

            if self.schedule == 'static':
                self.set_static_iterations_stacks()

            if self.schedule == 'guided' or self.schedule == 'dynamic':
                if self.chunk is None:
                    self.chunk = 1
            self.setup_complete = True

        else:
            while not self.setup_complete:
                continue


    def set_static_iterations_stacks(self):
        '''This function divides the work statically among the threads before
            they begin the loop. each item in the list is a stack of work chunks
            with id matching the list index'''
        if self.chunk is None:
            self.chunk = floor(self.total_iterations_remaining / self.num_threads)
            if self.chunk == 0:
                self.chunk = 1

        thread_index = 0

        while self.total_iterations_remaining > 0:
            if self.total_iterations_remaining < self.chunk:
                if self.count_up:
                    start = int(self.end - self.total_iterations_remaining * self.step)
                    end = int(self.end)
                    self.total_iterations_remaining -= int((end - start) / self.step)
                    self.static_iterations_stacks[thread_index].append((start, end, self.step))
                    thread_index += 1
                    if thread_index == self.num_threads:
                        thread_index = 0

                else:
                    start = int(self.end + self.total_iterations_remaining * self.step)
                    end = int(self.end)
                    self.total_iterations_remaining -= int((end - start) / self.step) * -1
                    self.static_iterations_stacks[thread_index].append((start, end, self.step))
                    thread_index += 1
                    if thread_index == self.num_threads:
                        thread_index = 0

            else:
                if self.count_up:
                    start = int(self.end - self.total_iterations_remaining * self.step)
                    end = int(start + self.chunk * self.step)
                    self.total_iterations_remaining -= int((end - start) / self.step)
                    self.static_iterations_stacks[thread_index].append((start, end, self.step))
                    thread_index += 1
                    if thread_index == self.num_threads:
                        thread_index = 0

                else:
                    start = int(self.end + self.total_iterations_remaining * self.step)
                    end = int(start + self.chunk * self.step)
                    self.total_iterations_remaining -= int((end - start) / self.step) * -1
                    self.static_iterations_stacks[thread_index].append((start, end, self.step))
                    thread_index += 1
                    if thread_index == self.num_threads:
                        thread_index = 0

    def done(self):
        #print('\n\n')
        if self.schedule == 'static':
            return len(self.static_iterations_stacks[get_current_thread_id()]) == 0
        return self.total_iterations_remaining == 0


    def request(self):
        #sleep(1)
        if self.schedule == 'static' or self.schedule is None:
            id = get_current_thread_id()
            if len(self.static_iterations_stacks[id]) == 0:
                #self.lock.release()
                return 0, 0, 1
            iters = self.static_iterations_stacks[id].popleft()
            #self.lock.release()
            return iters[0], iters[1], iters[2]

        self.lock.acquire()
        if self.schedule == 'dynamic':

            if self.total_iterations_remaining < self.chunk:
                if self.count_up:
                    start = int(self.end - self.total_iterations_remaining * self.step)
                    end = int(self.end)
                    self.total_iterations_remaining -= int((end - start) / self.step)
                else:
                    start = int(self.end + self.total_iterations_remaining * self.step)
                    end = int(self.end)
                    self.total_iterations_remaining -= int((end - start) / self.step) * -1

                '''print('start: ', start)
                print('end: ', end)
                print('remaining iters: ', self.total_iterations_remaining)
                print('thread: ', get_current_thread_id())'''
                self.lock.release()
                return start, end, self.step if self.count_up else self.step * -1
            else:
                if self.count_up:
                    start = int(self.end - self.total_iterations_remaining * self.step)
                    end = int(start + self.chunk * self.step)
                    self.total_iterations_remaining -= int((end - start) / self.step)
                else:
                    start = int(self.end + self.total_iterations_remaining * self.step)
                    end = int(start + self.chunk * self.step)
                    self.total_iterations_remaining -= int((end - start) / self.step) * -1

                '''print('start: ', start)
                print('end: ', end)
                print('remaining iters: ', self.total_iterations_remaining)
                print('thread: ', get_current_thread_id())
                print'''
                self.lock.release()
                return start, end, self.step if self.count_up else self.step * -1

        if self.schedule == 'guided':

            # the chunk size cannot go below the specified chunk size if there is one
            chunk = floor(self.total_iterations_remaining / self.num_threads)
            if chunk < self.chunk:
                chunk = self.chunk
            #print('chunk: ', chunk)


            if self.total_iterations_remaining < chunk:
                if self.count_up:
                    start = int(self.end - self.total_iterations_remaining * self.step)
                    end = int(self.end)
                    self.total_iterations_remaining -= int((end - start) / self.step)
                else:
                    start = int(self.end + self.total_iterations_remaining * self.step)
                    end = int(self.end)
                    self.total_iterations_remaining -= int((end - start) / self.step) * -1

                '''print('start: ', start)
                print('end: ', end)
                print('remaining iters: ', self.total_iterations_remaining)'''
                self.lock.release()
                return start, end, self.step if self.count_up else self.step * -1
            else:
                if self.count_up:
                    start = int(self.end - self.total_iterations_remaining * self.step)
                    end = int(start + chunk * self.step)
                    self.total_iterations_remaining -= int((end - start) / self.step)
                else:
                    start = int(self.end + self.total_iterations_remaining * self.step)
                    end = int(start + chunk * self.step)
                    self.total_iterations_remaining -= int((end - start) / self.step) * -1

                '''print('start: ', start)
                print('end: ', end)
                print('remaining iters: ', self.total_iterations_remaining)'''
                self.lock.release()
                return start, end, self.step if self.count_up else self.step * -1





