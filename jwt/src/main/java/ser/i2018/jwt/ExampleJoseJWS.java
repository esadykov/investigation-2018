package ser.i2018.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.ParseException;
import java.util.Base64;
import java.util.Objects;

public class ExampleJoseJWS {

    public static final String HELLO_WORLD = "Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!Hello world!!!!!!!!!!!!!!!!!!!!!!";

    public static void main(String[] args) {

        RSAPrivateKey privateKey = null;
        RSAPublicKey publicKey = null;
        KeyFactory rsa = null;
        try {
            rsa = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        assert rsa != null;
        try {
            //read private key
            //byte[] privateKeyBytes = Files.readAllBytes(Paths.get(Objects.requireNonNull(ExampleRSA.class.getClassLoader().getResource("sensor.pkcs8NoPEM")).toURI()));
            byte[] privateKeyBytes = Files.readAllBytes(Paths.get(Objects.requireNonNull(ExampleJoseJWS.class.getClassLoader().getResource("private_keypair.pem")).toURI()));
            String privateKeyString = (new String(privateKeyBytes))
                    .replaceAll("(-+BEGIN.*KEY-+\\r?\\n|-+END.*KEY-+\\r?\\n?|\\r?\\n)", "");
            System.out.println(privateKeyString);
            PKCS8EncodedKeySpec privateSpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyString.getBytes()));
            privateKey = (RSAPrivateKey) rsa.generatePrivate(privateSpec);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            //read public key
            byte[] publicBytes = Files.readAllBytes(Paths.get(Objects.requireNonNull(ExampleJoseJWS.class.getClassLoader().getResource("public_key.pem")).toURI()));
            String publicKeyString = (new String(publicBytes))
                    .replaceAll("(-+BEGIN.*KEY-+\\r?\\n|-+END.*KEY-+\\r?\\n?|\\r?\\n)", "");
//                    .replace("\n", "")
//                    .replace("-BEGIN PUBLIC KEY-", "")
//                    .replace("-END PUBLIC KEY-", "")
//                    .replace("-", "");
            System.out.println(publicKeyString);
            X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyString.getBytes()));
            publicKey = (RSAPublicKey) rsa.generatePublic(publicSpec);

        } catch (Exception e) {
            e.printStackTrace();
        }

        assert publicKey != null;
        assert privateKey != null;

        try {
// Create RSA-signer with the private key
            JWSSigner signer = new RSASSASigner(privateKey);

// Prepare JWS object with simple string as payload
            JWSObject jwsObject = new JWSObject(
                    new JWSHeader.Builder(JWSAlgorithm.RS512).build(),
                    new Payload(HELLO_WORLD));

// Compute the RSA signature
            jwsObject.sign(signer);

// To serialize to compact form, produces something like
// eyJhbGciOiJSUzI1NiJ9.SW4gUlNBIHdlIHRydXN0IQ.IRMQENi4nJyp4er2L
// mZq3ivwoAjqa1uUkSBKFIX7ATndFF5ivnt-m8uApHO4kfIFOrW7w2Ezmlg3Qd
// maXlS9DhN0nUk_hGI3amEjkKd0BWYCB8vfUbUv0XGjQip78AI4z1PrFRNidm7
// -jPDm5Iq0SZnjKjCNS5Q15fokXZc8u0A
            String s = jwsObject.serialize();

            System.out.println("-------------------");
            System.out.println(s);
            System.out.println("-------------------");

// To parse the JWS and verify it, e.g. on client-side
            jwsObject = JWSObject.parse(s);

            JWSVerifier verifier = new RSASSAVerifier(publicKey);

            System.out.println(jwsObject.verify(verifier));

            System.out.println(HELLO_WORLD.equals(jwsObject.getPayload().toString()));
        } catch (JOSEException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

}
