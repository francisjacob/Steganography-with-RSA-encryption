//Finding RSA
// By Francis Jacob
import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

public class RSA
{
    private BigInteger p;
    private BigInteger q;

    private BigInteger N;

    private BigInteger phi;

    private BigInteger e;
    private BigInteger d;

    private int bitlength = 1024;

    private Random r;

    public RSA()
    {
        r = new Random();
        p = BigInteger.probablePrime(bitlength, r);
        q = BigInteger.probablePrime(bitlength, r);

        /* p and q are two prime numbers which are relatively prime */

        N = p.multiply(q);

        //phi = (p-1)(q-1)
        phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

        // find a number e such that e is less than phi(n) and gcd(e,phi(n)) is 1
        e = BigInteger.probablePrime(bitlength / 2, r);

        while (phi.gcd(e).compareTo(BigInteger.ONE) > 0 && e.compareTo(phi) < 0)
        {
            e.add(BigInteger.ONE);
        }

        //d=e-1mod(phi(n))
        d = e.modInverse(phi);
    }

    public RSA(BigInteger e, BigInteger d, BigInteger N)
    {
        this.e = e;
        this.d = d;
        this.N = N;
    }

    public BigInteger returne()
    {
        return e;
    }

    public BigInteger returnd()
    {
        return d;
    }

    public BigInteger returnN()
    {
        return N;
    }


    private static String bytesToString(byte[] encrypted)
    {
        String test = "";
        for (byte b : encrypted)
        {
            test += Byte.toString(b);
        }
        return test;
    }

    // Encrypt message
    //C=pow(M,e)mod n
    public byte[] encrypt(byte[] message)
    {
        return (new BigInteger(message)).modPow(e, N).toByteArray();
    }

    // Decrypt message
    //M=pow(C,d)mod n
    public byte[] decrypt(byte[] message)
    {
        return (new BigInteger(message)).modPow(d, N).toByteArray();
    }

}
