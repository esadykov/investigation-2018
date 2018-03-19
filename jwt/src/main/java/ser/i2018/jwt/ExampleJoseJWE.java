package ser.i2018.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jose.crypto.RSAEncrypter;

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

public class ExampleJoseJWE {

    public static final String HELLO_WORLD = "Hello world!!!!!!!!!!!!!!!!!!!!!!";

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
            byte[] privateKeyBytes = Files.readAllBytes(Paths.get(Objects.requireNonNull(ExampleJoseJWE.class.getClassLoader().getResource("private_keypair.pem")).toURI()));
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
            byte[] publicBytes = Files.readAllBytes(Paths.get(Objects.requireNonNull(ExampleJoseJWE.class.getClassLoader().getResource("public_key.pem")).toURI()));
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
// Create the header
            JWEHeader header = new JWEHeader(JWEAlgorithm.RSA_OAEP, EncryptionMethod.A128GCM);

// Set the plain text
            Payload payload = new Payload(HELLO_WORLD);

// Create the JWE object and encrypt it
            JWEObject jweObject = new JWEObject(header, payload);
            jweObject.encrypt(new RSAEncrypter(publicKey));

// Serialise to compact JOSE form...
            String jweString = jweObject.serialize();

            System.out.println(jweString);
// Parse into JWE object again...
            jweObject = JWEObject.parse(jweString);

// Decrypt
            jweObject.decrypt(new RSADecrypter(privateKey));

// Get the plain text
            payload = jweObject.getPayload();
            assert HELLO_WORLD.equals(payload.toString());

        } catch (JOSEException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

}
