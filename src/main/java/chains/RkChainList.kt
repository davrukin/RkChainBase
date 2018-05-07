package chains

import blockchain.Block
import interfaces.BlockChain
import io.reactivex.rxkotlin.toObservable
import models.UserModel

class RkChainList(block: Block) : Iterable<Block>, BlockChain {

	private var difficulty: Int = 1
	private var head: Block ?= null
	private var numBlocks: Int = 0

	init {
		head = block
		numBlocks++
	}

	/**
	 * Adds at the head
	 */
	override fun prependBlock(block: Block) {
		val temp = head
		head = block
		head!!.next = temp
		numBlocks++
		repairHashes() // must go down the chain
	}

	/**
	 * Adds at the tail
	 */
	override fun appendBlock(block: Block) {
		var temp = head
		while (temp!!.next != null) {
			temp = temp.next
		}
		temp.next = block
		numBlocks++
		repairHashes()
	}

	override fun appendAllBlocks(vararg blocks: Block) {
		for (block in blocks) {
			appendBlock(block)
		}
	}

	override fun addAtIndex(index: Int, block: Block) {
		var temp = head
		var holder: Block ?= null

		temp = walk(index, temp)

		holder = temp!!.next!!
		temp.next = block
		temp.next!!.next = holder
		numBlocks++
		repairHashes()
	}

	override fun deleteAtIndex(index: Int) {
		// for all of these, must recalculate all hashes and check validity
		// maintain chain continuity
		// !!! refer to each element by its hash, not by its data
		var temp = head

		temp = walk(index, temp)

		temp!!.next = temp.next!!.next
		numBlocks--
		repairHashes()
	}

	private fun walk(index: Int, temp: Block?): Block? {
		var i = 0
		var temp1 = temp
		while (walker(i, index, temp1!!)) {
			temp1 = temp1.next
			i++
		}
		return temp1
	}

	private fun walker(i: Int, index: Int, temp: Block): Boolean {
		return (i < (index - 1)) && (temp.next != null)
	}

	override fun deleteBlock(block: Block) {
		deleteAtIndex(findByBlock(block))
	}

	override fun findByBlock(block: Block): Int {
		var temp = head
		var i = 0
		while (temp != block) {
			temp = temp!!.next
			i++
		}
		return i
	}

	override fun findByIndex(index: Int): Block {
		var temp = head
		var i = 0
		while (i < index) {
			temp = temp!!.next
			i++
		}
		return temp!!
	}

	// need to account for potential npe if hash doesn't exist or wasn't mined
	override fun findByHash(hash: String): Block {
		return this.toObservable().filter { b -> b.hash == hash }.blockingFirst()
	}

	override fun getPreviousBlock(block: Block): Block {
		return findByHash(block.previousHash!!)
	}

	override fun getNextBlock(block: Block): Block {
		return block.next()
	}

	override fun editBlock(index: Int, data: UserModel) {
		findByIndex(index).editBlock(data)
		repairHashes()
	}

	override fun editBlock(block: Block, data: UserModel) {
		findByIndex(findByBlock(block)).editBlock(data)
	}

	override fun size(): Int {
		return numBlocks
	}

	override fun isChainValid(): Boolean {
		var currentBlock: Block
		var previousBlock: Block
		val hashTarget = String(CharArray(difficulty)).replace('\u0000', '0') //Create a string with difficulty * "0"

		if (head != null) { // start with element 1, refer to element 0
			var e0 = head
			var next = head!!.next
			while (next != null) {
				currentBlock = next
				previousBlock = e0!!

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

				e0 = next
				next = next.next
			}
			return true
		} else {
			return true
		}	}

	override fun repairHashes() {
		if (!isChainValid()) {
			println("Blockchain Invalid. Recalculating Hashes.")
			//this.toObservable().subscribe { it.calculateHash() }
			if (head != null) {
				while (head!!.hasNext()) {
					head!!.calculateHash()
					head = head!!.next
				}
			}
		}	}

	override fun getTotalHash(): String {
		val hashSum = StringBuilder()
		this.toObservable().subscribe { hashSum.append(it.hash) }
		return Utilities.applySha256(hashSum.toString())	}

	override fun toJson(): String {
		return Utilities.objectToJsonString(this)
	}

	override fun print(): String {
		return toJson()
	}

	override fun toString(): String {
		return toJson()
	}

	/**
	 * Returns an iterator over the elements of this object.
	 */
	override fun iterator(): Iterator<Block> {
		return object : Iterator<Block> {

			private var currentIndex = 0

			override fun hasNext(): Boolean {
				return currentIndex < size() && findByIndex(currentIndex) != null
			}

			override fun next(): Block {
				return findByIndex(currentIndex++)
			}
		}
	}
}