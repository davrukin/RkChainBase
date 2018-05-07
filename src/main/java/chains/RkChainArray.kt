package chains

import blockchain.Block
import interfaces.BlockChain
import io.reactivex.Observable
import io.reactivex.rxkotlin.toObservable
import models.UserModel

/**
 * arraylist wrapper over blockchain
 */
class RkChainArray : Iterable<Block>, BlockChain {

	private var blockChain: ArrayList<Block> = ArrayList()
	private var difficulty: Int = 1

	/*fun addBlock(userModel: UserModel, previousHash: String, difficulty: Int) {
		blockChain.add(Block(userModel, previousHash, difficulty))
	}

	fun addBlocks(vararg blocks: Block) {
		blockChain.addAll(blocks)
	}*/

	override fun prependBlock(block: Block) {
		blockChain.add(0, block)
	}

	override fun appendAllBlocks(vararg blocks: Block) {
		blockChain.addAll(blocks)
	}

	override fun addAtIndex(index: Int, block: Block) {
		blockChain.add(index, block)
	}

	override fun deleteAtIndex(index: Int) {
		blockChain.removeAt(index)
	}

	override fun deleteBlock(block: Block) {
		blockChain.remove(block)
	}

	override fun getNextBlock(block: Block): Block {
		return block.next()
	}

	override fun print(): String {
		return toJson()
	}

	override fun isChainValid(): Boolean {
		var currentBlock: Block
		var previousBlock: Block
		val hashTarget = String(CharArray(difficulty)).replace('\u0000', '0') //Create a string with difficulty * "0"

		for (i in 1 until blockChain.size) {
			currentBlock = blockChain[i]
			previousBlock = blockChain[i - 1]
			//compare registered hash and calculated hash:
			if (currentBlock.hash != currentBlock.calculateHash()) {
				println("Current Hashes not equal")
				return false
			}
			//compare previous hash and registered previous hash
			if (previousBlock.hash != currentBlock.previousHash) {
				println("Previous Hashes not equal")
				return false
			}
			//check if hash is solved
			if (currentBlock.hash!!.substring(0, difficulty) != hashTarget) {
				println("This block hasn't been mined")
				return false
			}
		}

		return true
	}

	override fun repairHashes() {
		// maybe with iterator, using the next() method, it'll be easier to go through the blockchain
		if (!isChainValid()) {
			println("Blockchain Invalid. Recalculating Hashes.")
			//blockChain.toObservable().subscribe { it.calculateHash() }
			this.toObservable().subscribe { it.calculateHash() }
		}
	}

	override fun getTotalHash(): String {
		val hashSum = StringBuilder()
		blockChain.toObservable().subscribe { hashSum.append(it.hash) }
		return Utilities.applySha256(hashSum.toString())
	}

	override fun getPreviousBlock(block: Block): Block {
		return findByHash(block.previousHash!!)
	}

	override fun findByHash(hash: String): Block {
		return blockChain.toObservable().filter { it.hash == hash }.blockingFirst()
	}

	override fun appendBlock(block: Block) {
		blockChain.add(block)
	}

	override fun findByBlock(block: Block): Int {
		for ((i, b) in blockChain.withIndex()) {
			if (b == block) {
				return i
			}
		}
		return -1
	}

	override fun findByIndex(index: Int): Block {
		return blockChain[index]
	}

	override fun editBlock(index: Int, data: UserModel) {
		blockChain[index].editBlock(data)
	}

	override fun editBlock(block: Block, data: UserModel) {
		findByIndex(findByBlock(block)).editBlock(data)
	}


	override fun toJson(): String {
		return Utilities.objectToJsonString(blockChain)
	}

	override fun iterator(): Iterator<Block> {
		return blockChain.iterator()
	}

	/**
	 * Prints json version of the blockchain
	 */
	override fun toString(): String {
		return toJson()
	}

	override fun size(): Int {
		return blockChain.size
	}
}