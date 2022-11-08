import java.math.BigInteger;
import java.util.Random;


class RandomPrime{
    static int randomPrime(){
	Random random = new Random();
	return BigInteger.valueOf(random.nextInt(1073741823)).nextProbablePrime().intValue();
    }
    
}
