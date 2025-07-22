package com.example.coindesk.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CryptoUtilTest {

    @Test
    public void testEncryptAndDecrypt() {
        String original = "23342.0112";

        String encrypted = CryptoUtil.encrypt(original);
        assertNotNull(encrypted);
        // 加密結果應與原本不同
        assertNotEquals(original, encrypted);

        String decrypted = CryptoUtil.decrypt(encrypted);
        // 解密應還原
        assertEquals(original, decrypted);
    }

    @Test
    public void testDecryptInvalidData_shouldThrow() {
        String invalidEncrypted = "INVALIDDATA";

        assertThrows(RuntimeException.class, () -> {
            CryptoUtil.decrypt(invalidEncrypted);
        });
    }
}
