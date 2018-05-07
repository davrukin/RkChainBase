package interfaces

import blockchain.Block
import models.UserModel

interface BlockChain {

	fun prependBlock(block: Block)
	fun appendBlock(block: Block)
	fun appendAllBlocks(vararg blocks: Block)

	fun addAtIndex(index: Int, block: Block)
	fun deleteAtIndex(index: Int)
	fun deleteBlock(block: Block)

	fun findByBlock(block: Block): Int
	fun findByIndex(index: Int): Block
	fun findByHash(hash: String): Block

	fun getPreviousBlock(block: Block): Block
	fun getNextBlock(block: Block): Block

	fun editBlock(index: Int, data: UserModel)
	fun editBlock(block: Block, data: UserModel)

	fun size(): Int

	fun isChainValid(): Boolean
	fun repairHashes()
	fun getTotalHash(): String

	fun toJson(): String
	fun print(): String
}

/*open class BlockChain2() {

	open fun prependBlock(block: Block) {}
	fun appendBlock(block: Block) {}
	fun appendAllBlocks(vararg blocks: Block) {}

	fun addAtIndex(index: Int, block: Block) {}
	fun deleteAtIndex(index: Int) {}
	fun deleteBlock(block: Block) {}

	fun findByBlock(block: Block): Int {}
	fun findByIndex(index: Int): Block {}
	fun findByHash(hash: String): Block {}

	fun getPreviousBlock(block: Block): Block {}
	fun getNextBlock(block: Block): Block {}

	fun editBlock(index: Int, data: UserModel) {}
	fun editBlock(block: Block, data: UserModel) {}

	fun size(): Int {}

	fun isChainValid(): Boolean {}
	fun repairHashes() {}
	fun getTotalHash(): String {}

	fun toJson(): String {}
	fun print(): String {}
}

class sdf() : BlockChain2() {

	fun asd() {
		super.addAtIndex(0, null!!)

	}


	override fun prependBlock(block: Block) {
		super.prependBlock(block)
		println("Asd")
	}
}

class qwe() {
	fun asd() {
		val a = sdf()
		a.prependBlock()
	}
}*/