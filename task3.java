import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class task3 {
    public static void main(String[] args) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        int move_user_int = -1;
        String move_user = null;

        if (args.length < 3 || args.length % 2 == 0) {
            if (!isUnique(args))
                System.out.println("The number of moves must be greater than or equal to 3 and must be unpaired and possible moves must be unique.\nFor example: rock paper scissors.");
            else
                System.out.println("The number of moves must be greater than or equal to 3 and must be unpaired.\nFor example: rock paper scissors.");
            return;
        }

        if (!isUnique(args)) {
            System.out.println("Possible moves must be unique.\nFor example: rock paper scissors.");
            return;
        }

        int move_computer_int = (int) ((Math.random() * (args.length - 0)) + 0);
        String move_computer = args[move_computer_int];

        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        SecureRandom random = new SecureRandom();
        keyGen.init(random);
        SecretKey secretKey = keyGen.generateKey();
        String key = new BigInteger(1, secretKey.getEncoded()).toString(16);
        System.out.println("HMAC:\n" + calculateHMac(key, move_computer));
        System.out.println("Available moves:");
        for (int i = 0; i < args.length; i++) {
            System.out.println((i + 1) + " - " + args[i]);
        }
        System.out.println("0 - exit");

        System.out.print("Enter your move: ");
        move_user_int = Integer.parseInt(bufferedReader.readLine());
        if (move_user_int == 0)
            return;
        while (move_user_int <= 0 || move_user_int > args.length){
            System.out.print("There`s no such move. Try again.\nEnter your move: ");
            move_user_int = Integer.parseInt(bufferedReader.readLine());
        }
        move_user = args[move_user_int - 1];
        System.out.println("Your move: " + move_user);

        System.out.println("Computer move: " + move_computer);

        System.out.println(get_results(args, (move_user_int-1), move_computer_int));

        System.out.println("HMAC key:\n" + key);

    }
    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for(byte b: a)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }
    public static String calculateHMac(String key, String data) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");

        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
        sha256_HMAC.init(secret_key);

        return byteArrayToHex(sha256_HMAC.doFinal(data.getBytes("UTF-8")));
    }
    public static String get_results(String[] args, int move_user_int, int move_computer_int){
        int count_of_winners = (args.length-1)/2;
        ArrayList<String> who_win = new ArrayList<>(count_of_winners);
        ArrayList<String> who_lose = new ArrayList<>(count_of_winners);
        if (move_user_int == move_computer_int)
            return "Dead heat.";
        if (move_user_int + count_of_winners > args.length-1){
            for (int i = move_user_int-1; i >= move_user_int-count_of_winners; i --){
                who_lose.add(args[i]);
            }
        } else{
            for (int i = move_user_int+1; i <= move_user_int+count_of_winners; i++) {
                who_win.add(args[i]);
            }
        }
        if (who_win.isEmpty()){
            if (who_lose.contains(args[move_computer_int])){
                return "You win!";
            } else {
                return "You lose.";
            }
        } else{
            if (who_win.contains(args[move_computer_int])){
                return "You lose.";
            }else {
                return "You win!";
            }
        }
    }
    public static boolean isUnique(String[] args) {
        Set set = new HashSet();

        for(int i=0; i<args.length; i++) {
            if(!set.add(args[i])) {
                return false;
            }
        }
        return true;
    }
}
