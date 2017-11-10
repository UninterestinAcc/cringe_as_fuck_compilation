#![no_std]

mod rand;

pub struct Game {
	pub score: u64,
	// Other than 0 = empty tile, value of tile = 2^n
	pub tiles: [u8; 16],
	rng: rand::MSMRand
}

#[derive(Clone, Copy)]
pub enum Direction {
	Up,
	Down,
	Left,
	Right
}

#[derive(Clone, Copy)]
pub enum PromptingGameState {
	IllegalMove,
	Lose
}

impl Game {
	pub fn play(&mut self, dir: Direction) -> Option<PromptingGameState> {
		// TODO

		return if self.insert_random_tile() { None } else { Some(PromptingGameState::Lose) };
	}

	pub fn count_empty_squares(&self) -> u8 {
		let mut empty_squares_count = 0;
		for i in 0..16 {
			if self.tiles[i] == 0 {
				empty_squares_count += 1;
			}
		}
		return empty_squares_count;
	}

	fn insert_random_tile(&mut self) -> bool {
		let max = self.count_empty_squares() as u32;

		if max == 0 {
			return false;
		}

		let mut rand_i = 0;
		let rand = { self.rng.next() % max };

		for i in 0..16 {
			let empty_tile = { self.tiles[i] == 0 };
			if empty_tile {
				if rand_i < rand {
					rand_i += 1;
				} else {
					// Fill...
					self.tiles[i] = (self.rng.next() % 2) as u8 + 1;
					return true;
				}
			}
		}
		return false;
	}
}
