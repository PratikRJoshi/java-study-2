#! /usr/bin/env python3

"""Time the stream coders in bzip assignment. Print a list of times to stdout.
"""


import argparse
import subprocess
import errno
import time
import os
import sys
import csv
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

ACTIONS = ['+', '-', '+-']


class Class:

	"Context manager for compiling, using, then removing a class"

	def __init__(self, classname): self.classname = classname

	def __enter__(self):
		"Compile a class in the current working directory."
		subprocess.check_call(JAVAC + [self.classname + '.java'])
		printerr('Successfully compiled', self.classname)

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


def printerr(*args, **kws):
	"Print to stderr"
	print(*args, file=sys.stderr, flush=True, **kws)


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


def timesof(cmd, repeat):
	"Return list of repeat # of times for cmd. Print a dot to stderr for each."
	times = []
	for i in range(repeat):
		printerr('.', end='')
		times.append(timeof(cmd))
	printerr()
	return times


def parse_args(argv=None):
	parser = argparse.ArgumentParser(description=__doc__)
	parser.add_argument('classname', help='name of Java class to run')
	parser.add_argument('repeat', type=int, help='Number of timing runs to do')
	parser.add_argument('files', nargs='+', help='names of files to code')
	if argv is None:
		return parser.parse_args()
	else:
		return parser._parse_args(argv)


def main(argv=None):
	"Print time to run command to stdout."
	args = parse_args()
	times = []
	row = {'classname': args.classname}
	fieldnames = ['classname', 'file', 'bytes', 'action', 'time']
	with open(sys.stdout.fileno(), 'w', newline='', closefd=False) as stdout:
		with Class(args.classname):
			table = csv.DictWriter(stdout, fieldnames)
			for filename in args.files:
				printerr('Testing file', filename)
				filesize = os.path.getsize(filename)
				for action in ACTIONS:
					printerr('Action:', action)
					command = cmd(args.classname, filename, action)
					for obs in timesof(command, args.repeat):
						row.update({'time': obs, 'action': action,
									'bytes': filesize,
									'file': os.path.basename(filename)})
						table.writerow(row)
						times.append(obs)


if __name__ == '__main__':
	main(sys.argv[1:])
