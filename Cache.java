import java.util.ArrayList;
import java.util.HashMap;

// Direct Mapping

public class Cache {

	private Disk disk;
	private int[][] cache;
	private String byteInstr;
	private int wordsPerBlock;
	private int bytesPerBlock;
	private int numCacheBlocks;
	private double numTagBits;
	private double numIndexBits;
	private double numOffSetBits;
	private float cacheAccess;
	private float hit, miss;
	private float hitTime = 1.0f; // default
	private float missPenalty;

	public Cache() {
		disk = new Disk();
		byteInstr = "0x00";

		hit = 0;
		miss = 0;
		wordsPerBlock = 1; // default
		missPenalty = 10 + wordsPerBlock;
		numCacheBlocks = 4; // default
		bytesPerBlock = wordsPerBlock * 4;
		cacheAccess = 0;
		cache = new int[numCacheBlocks][2 + wordsPerBlock];

		if (wordsPerBlock == 1) {
			numOffSetBits = 0;
		} else {
			numOffSetBits = (int) Math.ceil(Math.log(wordsPerBlock));
		}

		numIndexBits = (int) Math.ceil(Math.log(numCacheBlocks));
		numTagBits = 32 - numIndexBits - numOffSetBits;

		cacheInfo();
	}

	/* USER FUNCTIONS */
	// addr -> 0xFF
	public void fetchAddr(String addr) {
		// Direct Map Cache
		cacheAccess += 1.0;
		byteInstr = addr;
		String[] tio = TIO(hexToBinary(addr));
		int tag = Integer.parseInt(tio[0], 2);
		int index = Integer.parseInt(tio[1], 2) % numCacheBlocks;

		int offset = 0;
		if (tio[2].length() > 0) {
			offset = Integer.parseInt(tio[2], 2);
		}

		System.out.println("Fetch Addr TIO: " + tag + " " + index + " " + offset);

		// MISS
		if (cache[index][1] != tag || cache[index][0] == 0 || cache[index][2 + offset] == 0) {

			// STORE DATA THEN RETURN
			miss += 1.0;
			storeTIO(tio); // store data in cache
			System.out.println("MISS at ADDR " + addr + " : "+ cache[index][2 + offset]);
		} 
		// HIT
		else if (cache[index][1] == tag && cache[index][0] != 0) {

			// RETURN DATA
			hit += 1.0;
			System.out.println("HIT at ADDR " + addr + " : "+ cache[index][2 + offset]);
		}
		return;
	}


	// convert hex into an Integer using the parseInt() radix parameter
	// calculate the input's Block Address and return it for parsing
	private String hexToBinary(String hex) {
		String binary = "";

		int blockAddr = calculateBlockAddress(Integer.parseInt(hex.substring(2, hex.length()), 16));

		// "binary" is the binary string of the Block Address used to generate TIO
		binary = padLeft(Integer.toBinaryString(blockAddr), 32); // default value of 8 bits
		return binary;
	}

	private String padLeft(String s, int i) {
		if (s.length() >= i) {
			return s;
		} else {
			while (s.length() != i) {
				s = "0" + s;
			}
			return s;
		}
	}

	private int getInt(String s, int i) {
		return Integer.parseInt(Character.toString(s.charAt(i)), 16);
	}

	// Block Addr = Byte Addr / Bytes per Block -> (1 word == 4 bytes == 32 bits)
	// Index = Block Addr % # of Blocks in Cache
	// Used to has index into the Cache
	private int calculateBlockAddress(int byteAddress) {
		int blockAddress = byteAddress / bytesPerBlock;
		return blockAddress;
	}

	private String[] TIO(String binary) {
		String offset = "";
		String index, tag;
		if (numOffSetBits != 0) {
			offset = binary.substring((int) (numTagBits + numIndexBits), 32);
		}
		tag = binary.substring(0, (int) numTagBits);
		index = binary.substring((int) numTagBits, (int) (numTagBits + numIndexBits));
		return new String[] {tag, index, offset};
	}

	private void storeTIO(String[] tio) {
		int tag, index, offset;
		tag = Integer.parseInt(tio[0], 2);
		index = Integer.parseInt(tio[1], 2) % numCacheBlocks;
		offset = 0;

		if (tio[2].length() > 0) {
			offset = Integer.parseInt(tio[2], 2);
		}

		int data = disk.getData(byteInstr);
		cache[index][1] = tag;
		cache[index][0] = 1; // valid bit
		cache[index][2 + offset] = data;

	}



	private class Disk {
		private HashMap<String, Integer> testInstructions;
		private String[] addr;
		private int index;

		public Disk() {
			index = 0;
			testInstructions = new HashMap<>(); //
			addr = new String[] {"0x00000000", "0x00000004", "0x00000008", "0x0000000c", "0x00000010"};
			int[] testData = new int[] {0, 1, 2, 3, 4};
			for (int i = 0; i < addr.length; i++) {
				testInstructions.put(addr[i], testData[i]);
			}
		}

		public int getData(String address) {
			int ret = testInstructions.get(address);
			index++;
			if (index % addr.length == 0) {
				index = 0;
			}
			return ret;
		}
	}


	/* DEBUGGING AND TESTING FUNCTIONS */

	private void cacheInfo() {

		System.out.println("Cache Info: ");
		System.out.println();
		System.out.println("Number of Blocks in Cache: " + numCacheBlocks + ", WordsPerBlock: " + wordsPerBlock + " ");
		System.out.println("Bytes Per Block: " + bytesPerBlock + ", Bits Per Block: " + (bytesPerBlock * 8));
		System.out.println("Number of Tag Bits: " + numTagBits + ", Number of Index Bits: " + numIndexBits + ", Number of Offset Bits: " + numOffSetBits);
		System.out.println();
	}

	public void printCache() {
		System.out.println("v T D");
		for (int i = 0; i < cache.length; i++) {
			for (int j = 0; j < cache[i].length; j++) {
				System.out.print(cache[i][j] + " ");
			}
			System.out.println();
		}
	}

	public void AMAT_Info() {
		System.out.println("Miss Penalty: " + missPenalty + ", Hit Time: " + hitTime);
	}

	public void cacheAccess() {
		System.out.println("Number of Cache Accesses: " + cacheAccess);
	}

	public void miss() {
		System.out.println("Misses: " + miss);
	}

	public void hits() {
		System.out.println("Hits: " + hit);
	}

	public void hitRate() {
		if (cacheAccess == 0) {
			return;
		}
		float x = (hit / cacheAccess);
		System.out.println("Hit Rate: " + x);
	}

	public void missRate() {
		if (cacheAccess == 0) {
			return;
		}
		float x = (miss / cacheAccess);
		System.out.println("Miss Rate: " + x);
	}

	public void AMAT() {
		if (cacheAccess == 0) {
			return;
		}
		System.out.println("AMAT: " + (hitTime + ((miss / cacheAccess) * missPenalty)));
	}


	public static void main(String[] args) {
		Cache cache = new Cache();
		String[] addr = new String[] {"0x00000000", "0x00000004", "0x00000008", "0x0000000c", "0x00000010", "0x00000010", "0x00000000"};

		for (String s : addr) {
			cache.fetchAddr(s);
			/*cache.cacheAccess();
			cache.hits();
			cache.miss();*/
		}
		//cache.hitRate();
		//cache.missRate();
		//cache.AMAT();

		cache.printCache();
	}
}