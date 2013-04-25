#! /usr/bin/env python3

"""Time the stream coders in bzip assignment. Print a list of times to stdout.
"""


import argparse
import subprocess
import errno
import time
import os
import sys
from math import sqrt


if os.name == 'posix':
	JAVA = ['java']
	JAVAC = ['javac']
else: # Everything is always harder on Windows...
	_ALGS4_HOME= os.sep.join(['C:', os.path.join('Users', 'wschwartz', 'algs4')])
	_STDLIB    = os.path.join(_ALGS4_HOME, 'stdlib.jar')
	_ALGS4     = os.path.join(_ALGS4_HOME, 'algs4.jar')
	_CLASSPATH = os.pathsep.join([_STDLIB, _ALGS4, os.curdir])
	JAVA       = ['java',  '-cp', _CLASSPATH]
	JAVAC      = ['javac', '-cp', _CLASSPATH]


class Class:

	"Context manager for compiling, using, then removing a class"

	def __init__(self, classname): self.classname = classname

	def __enter__(self):
		"Compile a class in the current working directory."
		subprocess.check_call(JAVAC + [self.classname + '.java'])

	def __exit__(self, exc_type, exc_value, traceback):
		"Remove object code from disk for directory hygine."
		os.unlink(self.classname + '.class')

class SubprocessError(Exception):

	"""Exception to use when os.system returns nonzero.

	Instantiate as SubprocessError(returncode, msg).
	"""

	def __init__(self, *args):
		super().__init__(*args)
		self.rc = args[0]
		self.msg = args[1]


def cmd(classname, filename, action):
	"Return the command to be run in a subprocess for timing"
	if not os.path.isfile(filename):
		raise OSError(errno.ENOENT, filename)
	if not os.path.isfile(classname + '.class'):
		raise OSError(errno.ENOENT, classname + '.class')
	cmd = JAVA + [classname]
	if action in ('+', '-'):
		cmd += [action, '<', filename]
	elif action in ('+-', '-+'):
		cmd += ['-', '<', filename, '|'] + JAVA + [classname, '+']
	else:
		raise ValueError('Unrecognized action: %r' % action)
	cmd += ['>', os.devnull]
	return ' '.join(cmd)


def timeof(cmd):
	"Return the amount of time in seconds `cmd` takes to run in a subshell."
	# subprocess.call has lots of overhead so use os.system.
	call, clock = os.system, time.perf_counter
	start = clock()
	rc = call(cmd)
	end = clock()
	if rc != 0:
		raise SubprocessError(rc, cmd)
	return end - start


def parse_args(argv=None):
	parser = argparse.ArgumentParser(description=__doc__)
	parser.add_argument('classname', help='name of Java class to run')
	parser.add_argument('action', choices=['-', '+', '-+', '+-'],
						help='encode: -, decode: +, both: -+ or +-.')
	parser.add_argument('file', help='name of file to code')
	parser.add_argument('repeat', type=int, help='Number of timing runs to do')
	if argv is None:
		return parser.parse_args()
	else:
		return parser._parse_args(argv)


def main(argv=None):
	"Print time to run command to stdout."
	args = parse_args()
	times = []
	with Class(args.classname):
		print('Running', args.classname, args.repeat, 'times', file=sys.stderr)
		command = cmd(args.classname, args.file, args.action)
		for i in range(args.repeat):
			times.append(timeof(command))
			print(times[-1])
	avg = sum(times)/len(times)
	print('average:', sum(times)/len(times), file=sys.stderr)
	stddev = sqrt(sum((xi - avg)**2 for xi in times) / (len(times) - 1))
	print('stddev :', stddev, file=sys.stderr)
	print('obs    :', len(times), file=sys.stderr)
	print('min    :', min(times), file=sys.stderr)
	print('max    :', max(times), file=sys.stderr)


if __name__ == '__main__':
	main(sys.argv[1:])
