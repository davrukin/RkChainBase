import blockchain.Block
import blockchain.BlockChain
import chains.RkChainArray
import chains.RkChainList
import models.UserModel
import io.reactivex.rxkotlin.toObservable

//import blockchain.blockchain

object Main {

    // RkChainBase: A Reactive Kotlin Blockchain Database
    // Reactive wrapper over Kotlin blockchain-based database

    // Mutable and Immutable versions, one extends the other and implements
    // edit/add/remove methods that with Rx modify each element's hash code based
    // on the previous (or all) elements, so that the chain doesn't get confused

    // databases.RkChainBaseMutable
    // databases.RkChainBaseImmutable

    // https://medium.com/programmers-blockchain/create-simple-blockchain-java-tutorial-from-scratch-6eeed3cb03fa


	private val genesisUserModel = UserModel()
	private val genesisBlock = Block(genesisUserModel, "0", 1)

	private val secondUserModel = UserModel()
	private val secondBlock = Block(secondUserModel, genesisBlock.hash!!, 1)

	private val thirdUserModel = UserModel()
	private val thirdBlock = Block(thirdUserModel, secondBlock.hash!!, 1)

	@JvmStatic
	fun main(args: Array<String>) {

		//test1()
		testArray()
		testList()
		// create wrapper functions for filtering and retrieving elements from the datastore
	}

	private fun printBlocks() {
		println("Hash for Genesis Block: ${genesisBlock.hash}")
		println("Hash for Second Block: ${secondBlock.hash}")
		println("Hash for Third Block: ${thirdBlock.hash}")
	}

	private fun test1() {
		printBlocks()

		var blockChain = BlockChain(genesisBlock)
		blockChain.addBlocks(secondBlock, thirdBlock)
		println("\nBlockchain Json: ${blockChain.toJson()}")

		var observable = blockChain.toObservable()

		println("-----\nTesting Linked List Implementation\n-----")
		// should refer to each element by hash
		blockChain = BlockChain(genesisBlock)
		blockChain.appendAll(secondBlock, thirdBlock)
		print(blockChain)
		print("IsValid? ${blockChain.isChainValid()}\n")
		print("Valid? ${blockChain.isValid()}\n")
		blockChain.find(1).editBlock(UserModel())
		print(blockChain)
		print("IsValid? ${blockChain.isChainValid()}\n")
		print("Valid? ${blockChain.isValid()}\n")
		blockChain.repairHashes()
		print("Hashes Repaired\n")
		print("IsValid? ${blockChain.isChainValid()}\n")
		print("Valid? ${blockChain.isValid()}\n")
	}


	private fun testArray() {
		println("testArray")
		printBlocks()

		val rkChainArray = RkChainArray()
		rkChainArray.appendAllBlocks(genesisBlock, secondBlock, thirdBlock)
		println(rkChainArray)

		println("IsValid? ${rkChainArray.isChainValid()}")
		rkChainArray.findByIndex(1).editBlock(UserModel())
		println("Edited Model")
		println(rkChainArray)
		println("IsValid? ${rkChainArray.isChainValid()}")
		rkChainArray.repairHashes()
		println("Hashes Repaired\n")
		println("IsValid? ${rkChainArray.isChainValid()}")
	}

	private fun testList() {
		println("testList")
		printBlocks()

		val rkChainList = RkChainList(genesisBlock)
		rkChainList.appendAllBlocks(secondBlock, thirdBlock)
		println(rkChainList)
		rkChainList.findByIndex(1).editBlock(UserModel())
		println("Edited Model")
		println(rkChainList)
		println("IsValid? ${rkChainList.isChainValid()}")
		rkChainList.repairHashes()
		println("Hashes Repaired\n")
		println("IsValid? ${rkChainList.isChainValid()}")
	}

}