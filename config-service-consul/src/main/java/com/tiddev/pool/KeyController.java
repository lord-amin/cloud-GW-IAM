package com.tiddev.pool;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class KeyController {
    private KeyPair keyPair;

    @GetMapping("/test")
    public Map<String, String> test() {
        return Collections.singletonMap("key", "value");
    }

    @GetMapping("/key")
    public Map<String, String> key(@RequestParam(value = "size", defaultValue = "2048") int size,
                                   @RequestParam(value = "reset", defaultValue = "false") boolean reset) {
        if(reset)
            keyPair=null;
        if (keyPair == null)
            keyPair = generateRsaKey(size);
        String publicK = new String(Base64.getEncoder().encode(keyPair.getPublic().getEncoded()));
        String privateK = new String(Base64.getEncoder().encode(keyPair.getPrivate().getEncoded()));
        return Map.of("public", publicK, "private", privateK, "kId", UUID.randomUUID().toString());
        //        return Map.of("public", keyPair.getPublic().getEncoded(), "private", keyPair.getPrivate().getEncoded(), "kId", UUID.randomUUID().toString().getBytes());
    }

    private static KeyPair generateRsaKey(int rsaLength) {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(rsaLength);
            keyPair = keyPairGenerator.generateKeyPair();
            System.out.println(keyPair.getPrivate());
            System.out.println(keyPair.getPublic());
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }
}
