import java.util.*;

public class RISCV {

	public RISCV() {

	}

	/*
	  Functions below will be converted to Aseembly Language
	  Then, converted from Assembly Language to Machine Language
	  Finally, the Machine Language will run through the Logisim Processor

	  1. Recursion
	  2. Branch
	  3. Loop
	  4. Helper/Function Call 

	  */


	// Factorial
	public int recursionTest(int n) {
		if (n <= 1) {
			return 1;
		} else {
			return n * recursionTest(n - 1);
		}
	}

	// even or odd
	public int branchTest(int n) {
		// even
		if (n % 2 == 0) {
			return 0;
		} else {
			return 1;
		}
	}

	// return number of even numbers between 0 and n,
	// exclusively and inclusively
	public int loopTest(int n) {
		if (n < 0) {
			return 0;
		}
		int i = 1;
		int sum = 0;
		while (i <= n) {
			if (i % 2 == 0) {
				sum++;
			}
			i++;
		}

		return sum;
	}

	// Square even numbers
	// return 1 otherwise
	public int helperFuncTest(int n) {
		
		if (n % 2 == 0) {
			return helperTest(n);
		} else {
			return 1;
		}
	}

	// Helper function for helperFuncTest()
	// return the square of n
	// calls an additional function to do this
	public int helperTest(int n) {
		return square(n);
	}

	// Helper function for helperTest()
	// demonstrates the sw/lw/sp uses
	// for Caller and Callee
	public int square(int n) {
		return n * n;
	}


	/*
	Calculate the Cycles per Instruction
	cycle == number of cycles in the pipeline
	jb == percentage of iunstructions that are jumps and/or branches
	frequency == frequency of times that branches and jumps are taken
	*/
	public double CPI(double cycle, double jb, double frequency) {
		return 0;
	}

	public static void main(String[] args) {
		RISCV riscv = new RISCV();
		System.out.println(riscv.recursionTest(5)); // 120

		System.out.println(riscv.branchTest(5)); // 1
		System.out.println(riscv.branchTest(4)); // 0

		System.out.println(riscv.loopTest(5)); // 2
		System.out.println(riscv.loopTest(16)); // 8 -> [16, 14, 12, 10, 8, 6, 4, 2]


		System.out.println(riscv.helperFuncTest(2)); // 4
		System.out.println(riscv.helperFuncTest(7)); // 1

	}
}