#! python3

"Test the Java stream coders in the bzip assignment."

import unittest
import subprocess
import glob
import os


TEST_DATA  = os.path.join('instructor', 'test')
MAXDIFFLENGTH = 800
if os.name == 'posix':
	JAVA = ['java']
	JAVAC = ['javac']
else:
	_ALGS4_HOME= os.sep.join(['C:', os.path.join('Users', 'wschwartz', 'algs4')])
	_STDLIB    = os.path.join(_ALGS4_HOME, 'stdlib.jar')
	_ALGS4     = os.path.join(_ALGS4_HOME, 'algs4.jar')
	_CLASSPATH = os.pathsep.join([_STDLIB, _ALGS4, os.curdir])
	JAVA       = ['java',  '-cp', _CLASSPATH]
	JAVAC      = ['javac', '-cp', _CLASSPATH]

class StreamTest:

	"Test encoding (-) and decoding (+) of binary streams by prog"

	prog = None
	ext = None # Includes the dot.

	@classmethod
	def setUpClass(cls):
		"Compile prog"
		subprocess.check_call(JAVAC + [cls.prog + '.java'])

	@classmethod
	def tearDownClass(cls):
		"Delete class files for directory hygine"
		for filename in glob.iglob(cls.prog + '*.class'):
			os.unlink(filename)

	def shortDescription(self):
		doc = self.__doc__.split('\n')[0] if self.__doc__ else ''
		return ' '.join([doc, self.prog])

	def test_encoding(self):
		"Test that prog can encode the same as reference data"
		encs = glob.glob(os.path.join(TEST_DATA, '*') + self.ext)
		raws = [os.path.splitext(i)[0] for i in encs]
		self._test_halftrip(raws, encs, '-', 'encoding')

	def test_decoding(self):
		"Test that prog can decode reference encoded data"
		encs = glob.glob(os.path.join(TEST_DATA, '*') + self.ext)
		raws = [os.path.splitext(i)[0] for i in encs]
		self._test_halftrip(encs, raws, '+', 'decoding')

	def _test_halftrip(self, from_files, to_files, cmd, action):
		"Test that prog can do one kind of coding"
		filename = None
		cmd = JAVA + [self.prog, cmd]
		for from_file, to_file in zip(from_files, to_files):
			with open(from_file, 'rb') as fromf:
				mine = subprocess.check_output(cmd, stdin=fromf)
			with open(to_file, 'rb') as tof:
				theirs = tof.read()
			if len(theirs) <= MAXDIFFLENGTH:
				self.assertSequenceEqual(mine, theirs,
										 '%s: %r' % (action, from_file))
			elif mine != theirs:
				self.fail('%s: %r' % (action, from_file))
		if filename is None:
			raise FileNotFoundError("Empty from or to lists")

	def test_roundtrip(self):
		"Test that prog can decode its own encoding"
		def cmd(f):
			return JAVA + [self.prog, '-', '<', f, '|'] + JAVA + [self.prog, '+']
		filename = None
		for filename in filter(os.path.isfile, os.listdir(TEST_DATA)):
			mine = subprocess.check_output(cmd(filename), shell=True)
			with open(filename, 'rb') as f:
				theirs = f.read()
			if len(theirs) <= MAXDIFFLENGTH:
				self.assertMultiLineEqual(mine, theirs,
										  'roundtrip: %r' % filename)
			elif mine != theirs:
				self.fail('roundtrip: %r' % filename)
		if filename is None:
			raise FileNotFoundError("No files tested")


class TestMoveToFront(StreamTest, unittest.TestCase):
	prog = 'MoveToFront'
	ext = 'mtf'


@unittest.skip("Takes too long until CircularSuffixArray is sped up")
class TestBurrowsWheeler(StreamTest, unittest.TestCase):
	prog = 'BurrowsWheeler'
	ext = 'bwt'


if __name__ == '__main__':
	unittest.main()
