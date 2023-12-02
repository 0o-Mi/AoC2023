package utils;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.stream.Collectors;

public class InputFetcher {

    private static final String AOC_URL = "https://adventofcode.com/";
    private static final String AOC_INPUT_ENDPOINT = "2023/day/{day}/input";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36";

    private final Path inputPath;
    private final int day;
    private final DayUtils.FetchOption fetchOption;

    public InputFetcher(Path inputPath, int day, DayUtils.FetchOption fetchOption) {
        this.inputPath = inputPath;
        this.day = day;
        this.fetchOption = fetchOption;
    }

    public void loadInputData() {
        try {
            switch (fetchOption) {
                case DONT_FETCH:
                    break;
                case FETCH_IF_EMPTY:
                    if (Files.notExists(inputPath)) {
                        Files.createFile(inputPath);
                        fetch();
                    }
                    break;
                case RE_FETCH:
                    fetch();
                    break;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void fetch() throws IOException {
        URL url = constructURL(day);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", USER_AGENT);
        connection.setRequestProperty("Cookie", "session=" + getSessionKey());
        try (
                BufferedInputStream bufferedInputStream = new BufferedInputStream(connection.getInputStream());
                BufferedWriter bufferedWriter = Files.newBufferedWriter(inputPath)
        ) {
            int read;
            while ((read = bufferedInputStream.read()) > 0) {
                bufferedWriter.write(read);
            }
        }
    }
    //Constructs a URL for a given day
    //I'm converting day into int here to prevent some edge cases where day could be '02' instead of '2'
    // so it reaches the correct endpoint

    private static URL constructURL(int day) {
        String url = AOC_URL + AOC_INPUT_ENDPOINT.replace("{day}", String.valueOf(day));
        System.out.println("Fetching from: " + url);
        try {
            return URL.of(URI.create(url), null);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Somehow this static URL is wrong.");
        }
    }
    //Specified in .env file in the root directory
    //this file is added in .gitignore, to be not shared online
    //It should contain one line looking like this:
    //session=YOUR_SESSION_COOKIE
    //You can get this value from your browser using the network tab in developer options
    //when looking at the request headers under "Cookie"

    private static String getSessionKey() throws IOException {
        Path dotenvPath = Path.of(DayUtils.RESOURCE_PATH + "cookie.env");
        HashMap<String, String> dotenvVariables;

        if (Files.notExists(dotenvPath))
            throw new RuntimeException("Couldn't find any dotenv files. Please create one in the root directory!");

        try (BufferedReader reader = Files.newBufferedReader(dotenvPath)) {
            dotenvVariables = new HashMap<>(reader.lines().collect(Collectors.toMap(s -> s.split(":")[0], s -> s.split(":")[1])));
        }
        return dotenvVariables.get("session");
    }
}
