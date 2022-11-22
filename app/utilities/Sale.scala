package utilities

import models.{analytics, blockchain, master}

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class Sale @Inject()(
                      collectionsAnalysis: analytics.CollectionsAnalysis,
                      blockchainBalances: blockchain.Balances,
                      masterAccounts: master.Accounts,
                      masterKeys: master.Keys,
                      masterWhitelists: master.Whitelists,
                      masterCollections: master.Collections,
                      masterNFTs: master.NFTs,
                      masterNFTOwners: master.NFTOwners,
                      masterSales: master.Sales,
                      masterWhitelistMembers: master.WhitelistMembers,
                    )(implicit executionContext: ExecutionContext) {

}