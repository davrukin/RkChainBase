package blockchain

import models.UserModel
import Utilities
import io.reactivex.rxkotlin.toObservable

// ll = linkedlist; al = arraylist; llal = both; n = neither
class BlockChain(block: Block) : Iterable<Block> {

	var blockChain: ArrayList<Block> = ArrayList()
	var difficulty: Int = 1
	private var index: Int = 0

	private var head: Block ?= null
	private var numBlocks: Int = 0

	// ll
	init {
		head = block
		numBlocks++
	}

	// ll
	fun addAtHead(block: Block) {
		val temp = head
		head = block
		head!!.next = temp
		numBlocks++
	}

	// ll
	fun addAtTail(block: Block) {
		var temp = head
		while (temp!!.next != null) {
			temp = temp.next
		}
		temp.next = block
		numBlocks++
	}

	// llal
	fun append(block: Block) {
		addAtTail(block)
	}

	// llal
	fun appendAll(vararg blocks: Block) {
		for (block in blocks) {
			addAtTail(block)
		}
	}

	// llal
	fun prepend(block: Block) {
		addAtHead(block)
	}

	// llal
	fun addAtIndex(index: Int, block: Block) {
		var temp = head
		var holder: Block ?= null
		var i = 0
		while (i < index - 1 && temp!!.next != null) {
			temp = temp.next
			i++
		}
		holder = temp!!.next!!
		temp.next = block
		temp.next!!.next = holder
		numBlocks++
	}

	// llal
	fun deleteAtIndex(index: Int) {
		// for all of these, must recalculate all hashes and check validity
		// maintain chain continuity
		// !!! refer to each element by its hash, not by its data
		var temp = head
		var i = 0
		while (i < index - 1 && temp!!.next != null) {
			temp = temp.next
			i++
		}
		temp!!.next = temp.next!!.next
		numBlocks--
	}

	// llal
	fun find(block: Block): Int {
		var temp = head
		var i = 0
		while (temp != block) {
			i++
			temp = temp!!.next
		}
		return i
	}

	// llal
	fun find(index: Int): Block {
		var temp = head
		var i = 0
		while (i < index) {
			temp = temp!!.next
			i++
		}
		return temp!!
	}

	// llal
	fun editBlock(index: Int, data: UserModel) {
		val b = find(index)
		b.editBlock(data)
	}

	// llal
	fun editBlock(block: Block, data: UserModel) {
		val b: Block = find(find(block))
		b.editBlock(data)
	}

	// llal
	fun printBlockChain(): String {
		val string = StringBuilder()
		var temp = head
		while (temp != null) {
			//println(temp.toString())
			string.append(temp.toString() + "\n")
			temp = temp.next
		}
		return string.toString()
	}

	// llal
	fun size(): Int {
		return numBlocks
	}

	// n
	fun addBlock(block: Block) {
		blockChain.add(block)
	}

	// n
	fun addBlock(userModel: UserModel, previousHash: String, difficulty: Int) {
		blockChain.add(Block(userModel, previousHash, difficulty))
	}

	// n
	fun addBlocks(vararg blocks: Block) {
		blockChain.addAll(blocks)
	}

	object Operations {
		object Adding {

		}
	}

	// llal
	// any change to the blockchain's blocks will cause this method to return false
	fun isChainValid(): Boolean {
		var currentBlock: Block
		var previousBlock: Block
		val hashTarget = String(CharArray(difficulty)).replace('\u0000', '0') //Create a string with difficulty * "0"

		/*var bcObservable = blockChain.toObservable()
				.skip(1)
				.doOnNext {
					currentBlock = it
					previousBlock = getPreviousBlock(it)
				}
				.filter {
					if ()
				}
				.subscribe( {

				})*/

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

	// n, ll
	fun isValid(): Boolean {
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
		}
	}

	// llal
	fun repairHashes() {
		// maybe with iterator, using the next() method, it'll be easier to go through the blockchain
		if (!isChainValid()) {
			println("Blockchain Invalid. Recalculating Hashes.")
			//blockChain.toObservable().subscribe { it.calculateHash() }
			this.toObservable().subscribe { it.calculateHash() }
		}
		if (!isValid()) {
			println("Blockchain Invalid. Recalculating Hashes.")
			//this.toObservable().subscribe { it.calculateHash() }
			if (head != null) {
				while (head!!.hasNext()) {
					head!!.calculateHash()
					head = head!!.next
				}
			}
		}
	}

	// llal
	fun getTotalHash(): String {
		val hashSum = StringBuilder()
		blockChain.toObservable().subscribe { hashSum.append(it.hash) }
		return Utilities.applySha256(hashSum.toString())
	}

	// llal
	private fun getPreviousBlock(block: Block): Block {
		return findByHash(block.previousHash!!)
	}

	// llal
	private fun findByHash(hash: String): Block {
		return blockChain.toObservable().filter { it.hash == hash }.blockingFirst()
	}

	// llal
	fun toJson(): String {
		return Utilities.objectToJsonString(blockChain)
	}

	// implement iterable here so it can be made into an observable
	override fun iterator(): Iterator<Block> {
		//TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
		return object : Iterator<Block> {

			private var currentIndex = 0

			override fun hasNext(): Boolean {
				return currentIndex < size() && find(currentIndex) != null
			}

			override fun next(): Block {
				return find(currentIndex++)
			}
		}
	}

	override fun toString(): String {
		return printBlockChain()
	}
}