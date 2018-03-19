package ser.i2018.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Objects;

public class ExampleRSA {
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
            byte[] privateKeyBytes = Files.readAllBytes(Paths.get(Objects.requireNonNull(ExampleRSA.class.getClassLoader().getResource("private_keypair.pem")).toURI()));
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
            byte[] publicBytes = Files.readAllBytes(Paths.get(Objects.requireNonNull(ExampleRSA.class.getClassLoader().getResource("public_key.pem")).toURI()));
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

        try {

            Algorithm clientAlgorithm = Algorithm.RSA256(null, privateKey);
            ObjectMapper mapper = new ObjectMapper();
            PayloadDto value = new PayloadDto();
            value.setAttr("attr_value");
            String token = JWT.create()
                    .withClaim("payload", mapper.writeValueAsString(value))
                    .sign(clientAlgorithm);

            System.out.println(token);

            DecodedJWT jwt = JWT.decode(token);
            System.out.println(new String(Base64.getDecoder().decode(jwt.getHeader())));
            System.out.println((jwt.getClaim("payload").as(PayloadDto.class).getAttr()));

            Algorithm serverAlgorithm = Algorithm.RSA256(publicKey, null);

            JWTVerifier verifier = JWT.require(serverAlgorithm)
                    .build(); //Reusable verifier instance

            verifier.verify(token);

            try {
                String badTokenSign = token + '1';
                verifier.verify(badTokenSign);
            } catch (SignatureVerificationException exception) {
                System.out.println("good");
            }

            try {
                String badTokenHead = new String(Base64.getEncoder().encode("{\"typ\":\"JWT\",\"alg\":\"RSA512\"}".getBytes()))
                        + token.substring(token.indexOf('.'));
                verifier.verify(badTokenHead);
            } catch (AlgorithmMismatchException exception) {
                System.out.println("good");
            }

            try {
                String badTokenPayload = jwt.getHeader() + "." + new String(Base64.getEncoder().encode("{\"typ\":\"JWT\",\"alg\":\"HS512\"}".getBytes())) + "." + jwt.getSignature();
                System.out.println("badTokenPayload");
                System.out.println(badTokenPayload);
                verifier.verify(badTokenPayload);
            } catch (SignatureVerificationException exception) {
                System.out.println("good");
            }

        } catch (JWTCreationException | JsonProcessingException e) {
            //Invalid Signing configuration / Couldn't convert Claims.
            e.printStackTrace();
        }
    }
}
