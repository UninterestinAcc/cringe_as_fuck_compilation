/// A random number generator using the [middle-square method](https://en.wikipedia.org/wiki/Middle-square_method)
/// Don't use for anything where you require random numbers, because this is actually not random.
pub struct MSMRand {
	seed: u32
}

impl MSMRand {
	/// Creates a new Middle-square method random number generator with the default seed
	pub fn new() -> MSMRand {
		MSMRand {
			seed: 214622
		}
	}

	/// Generates the next random number in the sequence
	pub fn next(&mut self) -> u32 {
		let square = self.seed * self.seed; // Square the seed

		let len = len(square); // Length of number in base10
		let trim = (len - 6) / 2; // Take the end index of the middle 6 numbers
		let out = (square / pow(10, trim)) % 1000000; // Extract the middle 6 numbers
		self.seed = out;

		return out;
	}
}

/// Finds the length of a number in base10
fn len(mut num: u32) -> u32 {
	let mut len = 0;
	while num >= 1 {
		len += 1;
		num /= 10;
	}
	return len;
}

/// Raise num by the order-th power
fn pow(num: u32, order: u32) -> u32 {
	let mut out = 1;
	for _ in 0..order {
		out *= num;
	}
	return out;
}
