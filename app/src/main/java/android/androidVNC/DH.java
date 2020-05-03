package android.androidVNC;
// CRYPTO LIBRARY FOR EXCHANGING KEYS
// USING THE DIFFIE-HELLMAN KEY EXCHANGE PROTOCOL

// The diffie-hellman can be used to securely exchange keys
// between parties, where a third party eavesdropper given
// the values being transmitted cannot determine the key.

// Implemented by Lee Griffiths, Jan 2004.
// This software is freeware, you may use it to your discretion,
// however by doing so you take full responsibility for any damage
// it may cause.

// Hope you find it useful, even if you just use some of the functions
// out of it like the prime number generator and the XtoYmodN function.

// It would be great if you could send me emails to: lee.griffiths@first4internet.co.uk
// with any suggestions, comments, or questions!

// Enjoy.

// Adopted to ms-logon for ultravnc and ported to Java by marscha, 2006.

//import java.lang.Math;

public class DH {

        public DH() {
                maxNum = (((long) 1) << DH_MAX_BITS) - 1;
        }

        public DH(long generator, long modulus) throws Exception {
                maxNum = (((long) 1) << DH_MAX_BITS) - 1;
                if (generator >= maxNum || modulus >= maxNum)
                        throw new Exception("Modulus or generator too large.");
                gen = generator;
                mod = modulus;
        }

        private long rng(long limit) {
                return (long) (java.lang.Math.random() * limit);
        }

        //Performs the miller-rabin primality test on a guessed prime n.
        //trials is the number of attempts to verify this, because the function
        //is not 100% accurate it may be a composite.  However setting the trial
        //value to around 5 should guarantee success even with very large primes
        private boolean millerRabin (long n, int trials) { 
                long a = 0; 

                for (int i = 0; i < trials; i++) { 
                        a = rng(n - 3) + 2;// gets random value in [2..n-1] 
                        if (XpowYmodN(a, n - 1, n) != 1) return false; //n composite, return false 
                }
                return true; // n probably prime 
        } 

        //Generates a large prime number by
        //choosing a randomly large integer, and ensuring the value is odd
        //then uses the miller-rabin primality test on it to see if it is prime
        //if not the value gets increased until it is prime
        private long generatePrime() {
                long prime = 0;

                do {
                        long start = rng(maxNum);
                        prime = tryToGeneratePrime(start);
                } while (prime == 0);
                return prime;
        }
         
        private long tryToGeneratePrime(long prime) {
                //ensure it is an odd number
                if ((prime & 1) == 0)
                        prime += 1;

                long cnt = 0;
                while (!millerRabin(prime, 25) && (cnt++ < DH_RANGE) && prime < maxNum) {
                        prime += 2;
                        if ((prime % 3) == 0) prime += 2;
                }
                return (cnt >= DH_RANGE || prime >= maxNum) ? 0 : prime;
        }
         
        //Raises X to the power Y in modulus N
        //the values of X, Y, and N can be massive, and this can be 
        //achieved by first calculating X to the power of 2 then 
        //using power chaining over modulus N
        private long XpowYmodN(long x, long y, long N) {
                long result = 1;
                final long oneShift63 = ((long) 1) << 63;

                for (int i = 0; i < 64; y <<= 1, i++){
                        result = result * result % N;
                        if ((y & oneShift63) != 0)
                                result = result * x % N;
                }
                return result;
        }

        public void createKeys() {
                gen = generatePrime();
                mod = generatePrime();

                if (gen > mod) {
                        long swap = gen;
                        gen  = mod;
                        mod  = swap;
                }
        }

        public long createInterKey() {
                priv = rng(maxNum);
                return pub = XpowYmodN(gen,priv,mod);
        }

        public long createEncryptionKey(long interKey) throws Exception {
                if (interKey >= maxNum){
                        throw new Exception("interKey too large");
                }
                return key = XpowYmodN(interKey,priv,mod);
        }


        public long getValue(int flags) {
                switch (flags) {
                        case DH_MOD:
                                return mod;
                        case DH_GEN:
                                return gen;
                        case DH_PRIV:
                                return priv;
                        case DH_PUB:
                                return pub;
                        case DH_KEY:
                                return key;
                        default:
                                return (long) 0;
                }
        }

        public int bits(long number){
                for (int i = 0; i < 64; i++){
                        number /= 2;
                        if (number < 2) return i;
                }
                return 0;
        }

        public static byte[] longToBytes(long number) {
                byte[] bytes  = new byte[8];
                for (int i = 0; i < 8; i++) {
                        bytes[i] = (byte) (0xff & (number >> (8 * (7 - i))));
                }
                return bytes;
        }

        public static long bytesToLong(byte[] bytes) {
                long result = 0;
                for (int i = 0; i < 8; i++) {
                        result <<= 8;
                        result += (byte) bytes[i];
                }
                return result;
        }

        private long gen;
        private long mod;
        private long priv;
        private long pub;
        private long key;
        private long maxNum;

        private static final int DH_MAX_BITS = 31;
        private static final int DH_RANGE = 100;

        private static final int DH_MOD  = 1;
        private static final int DH_GEN  = 2;
        private static final int DH_PRIV = 3;
        private static final int DH_PUB  = 4;
        private static final int DH_KEY  = 5;

}