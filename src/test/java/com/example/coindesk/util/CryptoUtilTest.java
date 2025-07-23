package com.example.coindesk.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

public class CryptoUtilTest {

    @Autowired
    private CryptoUtil cryptoUtil;

    @Test
    public void testEncryptAndDecrypt() {
        String original = "23342.0112";

        String encrypted = cryptoUtil.encrypt(original);
        assertNotNull(encrypted);
        // 加密結果應與原本不同
        assertNotEquals(original, encrypted);

        String decrypted = cryptoUtil.decrypt(encrypted);
        // 解密應還原
        assertEquals(original, decrypted);
    }

    @Test
    public void testDecryptInvalidData_shouldThrow() {
        String invalidEncrypted = "INVALIDDATA";

        assertThrows(RuntimeException.class, () -> {
            cryptoUtil.decrypt(invalidEncrypted);
        });
    }
}
