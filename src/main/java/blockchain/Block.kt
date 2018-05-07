package blockchain

//import models.UserModel
import Utilities
import models.UserModel
import java.util.*

class Block(data: UserModel, previousHash: String, difficulty: Int): Iterator<Block> {

	var hash: String ?= null // figure out how to make data structure refer by hash rather than by data
    var previousHash: String ?= previousHash
    private var data: UserModel?= data
    private var timeStamp: Long ?= null // created and edited?
	private var nonce: Int = 0
	//var index = 0
	private var difficulty: Int = 0

	var next: Block?= null

	init {
		this.timeStamp = Date().time
		this.hash = calculateHash()
		this.difficulty = difficulty
		mineBlock(this.difficulty)
	}

	// fun editBlock also has to modify references

	fun editBlock(data: UserModel) {
		this.data = data
		this.timeStamp = Date().time
		mineBlock(this.difficulty)
	}

	fun calculateHash(): String {
		return Utilities.applySha256(previousHash + timeStamp.toString() + Integer.toString(nonce) + data.toString())
	}

	fun mineBlock(difficulty: Int) {
		//val target = String(CharArray(difficulty)).replace('\0', '0')
		val target = String(CharArray(difficulty)).replace('\u0000', '0') //Create a string with difficulty * "0"
		while (hash!!.substring(0, difficulty) != target) { // asserted not null, maybe check for this later
			nonce++
			hash = calculateHash()
		}
		println("Block mined!!! : $hash")
	}

	override fun hasNext(): Boolean {
		return next != null
	}

	override fun next(): Block {
		// findBlockByHash(), iterate through blockchain.blockchain to see if element's hash matches the target hash
		if (hasNext()) {
			return next()
		} else {
			return null!!
		}
	}

	override fun toString(): String {
		return Utilities.objectToJsonString(this)
	}

	override fun equals(other: Any?): Boolean {
		val block = other as Block
		return block.hash == this.hash
	}

	override fun hashCode(): Int {
		return hash!!.hashCode()
	}
}
