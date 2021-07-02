
import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;
import com.google.gson.Gson;
import pojo.Location;
import pojo.Response;
import pojo.Transaction;
import utility.CurrencyFormatter;
import utility.DatabaseConnection;
import utility.DateFormatter;

public class Result {
    /*
     * complete the getUserTransaction function below.
     *
     * The function is expected to return an INTEGER_ARRAY.
     * The function accepts the following parameters:
     * INTEGER uid
     * STRING txnType
     * STRING monthYear
     *
     * https://jsonmock.hackerrank.com/api/transactions/search?userId=
     *
     */

    public static void getUserTransaction(int uid, String txnType, String monthYear) throws IOException {

        //get transactions json data from the api endpoint (first page)
        int page = 1,total_pages=0;
      String jsonString = getJsonString(uid, page,total_pages);

        //Using google's gson api, convert json-string to java object (pojo)
        Gson gson = new Gson();
        Response response = gson.fromJson(jsonString, Response.class);
        page = response.getPage() + 1;
        total_pages = response.getTotal_pages();
        List<Transaction> allTransactions = new ArrayList<>(response.getData());

        //get json data from other pages (paginated response) and add to the initial daata from the page 1
        while (page <= total_pages){
            String jsonStringNew = getJsonString(uid,page,total_pages);
            Response response1 = gson.fromJson(jsonStringNew, Response.class);
            for (Transaction transaction:response1.getData())
            allTransactions.add(transaction);
            page++;
        }

        System.out.println(allTransactions);

        //step1:Filter the user transactions for the given month-year
        List<Transaction> allMonthlyTransactions = allTransactions.stream().filter(obj -> DateFormatter.millisecondsToDate(obj.getTimestamp(), "MM-YYYY").equals(monthYear))
                .collect(Collectors.toList());


        //step2:Filter txnType=debit records for user in the given month,will be used to calculate monthly average spending
        List<BigDecimal> debitTransactions = allMonthlyTransactions.stream().filter(obj -> obj.getTxnType().equals("debit"))
                .map(obj -> {
                    try {
                        return CurrencyFormatter.parseCurrency(obj.getAmount(), Locale.US);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return null;
                }).collect(Collectors.toList());

        //step3:calculate the average monthly spending for the given month
        BigDecimal average = debitTransactions.stream().reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(debitTransactions.size()), 2, BigDecimal.ROUND_HALF_UP);


        //step4:Filter IDS by transactions for the given month by txnType and amount > avg-monthly-spending
        List<Integer> finalIDS = allMonthlyTransactions.stream().filter(obj -> obj.getTxnType().equals(txnType))
                .filter(obj -> {
                    try {
                        int value = CurrencyFormatter.parseCurrency(obj.getAmount(), Locale.US).compareTo(average);
                        if (value == 1)
                            return true;
                        else
                            return false;
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return false;

                })
                .map(obj -> obj.getId()).collect(Collectors.toList());


        System.out.println(finalIDS);
    }

    public static String getJsonString(int uid, int page,int total_pages) throws IOException {
        String jsonString = "";
       // new URL("");
        URL url;
        //Pass the desired URL as an object:
        if (page == 1)
        {
         url = new URL("https://jsonmock.hackerrank.com/api/transactions/search?userId=" + uid);
        }
        else if (page > 1 && page <= total_pages)
        {
           url = new URL("https://jsonmock.hackerrank.com/api/transactions/search?userId=" + uid + "&page="+page);
        }else
            return jsonString;

        //Type cast the URL object into a HttpURLConnection object
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // Set the request type, as in, whether the request to the API is a GET request or a POST request.
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "Application/json");
        //Open a connection stream to the corresponding API.
        conn.connect();
        int responseCode = conn.getResponseCode();
        if (responseCode != 200)
            throw new RuntimeException("HttpResponsecode: " + responseCode);
        else {
            //Write all the JSON data into a string using a scanner
            Scanner scanner = new Scanner(url.openStream());
            while (scanner.hasNext())
                jsonString += scanner.nextLine();
            scanner.close();
            return jsonString;
        }

    }

    public static void storeJsonData() throws IOException {
        URL url = new URL("https://jsonmock.hackerrank.com/api/transactions");
        String jsonString = "";
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "Application/json");
        conn.connect();
        if (conn.getResponseCode() != 200)
            throw new RuntimeException("Response code:"+conn.getResponseCode());
        else
        {
         Scanner scanner = new Scanner(url.openStream());
         while (scanner.hasNext())
             jsonString += scanner.nextLine();
         scanner.close();
        }

        Gson gson = new Gson();
        Response response = gson.fromJson(jsonString, Response.class);
        List<Transaction> transactions = response.getData();
        Set<Location> locations = transactions.stream().map(obj -> obj.getLocation())
                                 .collect(Collectors.toSet());


        //connect to the mysql database and insert all the transactions
        try {
            Connection connection = DatabaseConnection.getConnection();
            String sql = "insert into transactions values(?,?,?,?,?,?,?)";
            PreparedStatement ps = connection.prepareStatement(sql);

            int count =0;
            for (Transaction transaction:transactions){
                ps.setInt(1, transaction.getUserId());
                ps.setString(2,transaction.getUserName());
                ps.setLong(3,transaction.getTimestamp());
                ps.setString(4,transaction.getTxnType());
                ps.setString(5,transaction.getAmount());
                ps.setInt(6, transaction.getLocation().getId());
                ps.setString(7, transaction.getIp());
                ps.addBatch();
                count++;
                //execute every 100 rows or less
                if(count % 100 == 0 || count == transactions.size())
                    ps.executeBatch();
            }

          //  connection.commit();

            //insert location objects to the db
        PreparedStatement ps2 = connection.prepareStatement("insert into location values(?,?,?,?");
        int count2 =0;
        for (Location location:locations){
            ps2.setInt(1,location.getId());
            ps2.setString(2,location.getAddress());
            ps2.setString(3,location.getCity());
            ps2.setString(4,location.getZipCode());
            ps2.addBatch();
            count2++;
            if (count2 % 100 == 0 || locations.size() == count2)
                ps2.executeBatch();
        }

            connection.close();
        }catch (SQLException e){
            System.err.println(e.getMessage());
        }


    }

    public static void main(String[] args) throws IOException {
        //getUserTransaction(4, "debit", "02-2019");
        storeJsonData();
    }
}
