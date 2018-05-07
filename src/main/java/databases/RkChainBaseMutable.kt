package databases

import blockchain.Block
import chains.RkChainList
import models.UserModel

class RkChainBaseMutable {

	private var rkChainBaseMutable: RkChainList?= null

	/**
	 * Initializes the chainbase with a given block
	 * @param block Block, the given block
	 */
	fun initialize(block: Block) {
		this.rkChainBaseMutable = RkChainList(block)
	}

	/**
	 * Initializes the chainbase with a new block.
	 *
	 * Blank UserModel, empty PreviousHash, 0 difficulty
	 */
	fun initialize() {
		this.rkChainBaseMutable = RkChainList(Block(UserModel(), "", 0))
	}

	fun test() {
	}


}