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

import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class Example {
    public static void main(String[] args) {

        try {

            Algorithm algorithm = Algorithm.HMAC256("supersecret");
            ObjectMapper mapper = new ObjectMapper();
            PayloadDto value = new PayloadDto();
            value.setAttr("attr_value");
            String token = JWT.create()
                    .withClaim("payload", mapper.writeValueAsString(value))
                    .sign(algorithm);

            System.out.println(token);

            DecodedJWT jwt = JWT.decode(token);
            System.out.println(new String(Base64.getDecoder().decode(jwt.getHeader())));
            System.out.println((jwt.getClaim("payload").as(PayloadDto.class).getAttr()));

            JWTVerifier verifier = JWT.require(algorithm)
                    .build(); //Reusable verifier instance

            try {
                String badTokenSign = token + '1';
                verifier.verify(badTokenSign);
            } catch (SignatureVerificationException exception) {
                System.out.println("good");
            }

            try {
                String badTokenHead = new String(Base64.getEncoder().encode("{\"typ\":\"JWT\",\"alg\":\"HS512\"}".getBytes()))
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

        } catch (JWTCreationException | UnsupportedEncodingException | JsonProcessingException exception) {
            //Invalid Signing configuration / Couldn't convert Claims.
        }
    }
}
