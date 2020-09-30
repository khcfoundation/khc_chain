package io.fortest.core.db.accountstate;

import com.google.protobuf.ByteString;
import com.google.protobuf.Internal;
import java.util.Arrays;

import io.fortest.common.crypto.Hash;
import io.fortest.core.capsule.BlockCapsule;
import io.fortest.core.db.Manager;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import io.fortest.core.db.accountstate.storetrie.AccountStateStoreTrie;

@Slf4j(topic = "AccountState")
@Component
public class TrieService {

  @Setter
  private Manager manager;

  @Setter
  private AccountStateStoreTrie accountStateStoreTrie;

  public byte[] getFullAccountStateRootHash() {
    long latestNumber = manager.getDynamicPropertiesStore().getLatestBlockHeaderNumber();
    return getAccountStateRootHash(latestNumber);
  }

  public byte[] getSolidityAccountStateRootHash() {
    long latestSolidityNumber = manager.getDynamicPropertiesStore().getLatestSolidifiedBlockNum();
    return getAccountStateRootHash(latestSolidityNumber);
  }

  private byte[] getAccountStateRootHash(long blockNumber) {
    long latestNumber = blockNumber;
    byte[] rootHash = null;
    try {
      BlockCapsule blockCapsule = manager.getBlockByNum(latestNumber);
      ByteString value = blockCapsule.getInstance().getBlockHeader().getRawData()
          .getAccountStateRoot();
      rootHash = value == null ? null : value.toByteArray();
      if (Arrays.equals(rootHash, Internal.EMPTY_BYTE_ARRAY)) {
        rootHash = Hash.EMPTY_TRIE_HASH;
      }
    } catch (Exception e) {
      logger.error("Get the {} block error.", latestNumber, e);
    }
    return rootHash;
  }
}