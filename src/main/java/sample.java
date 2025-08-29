import io.github.bonigarcia.wdm.WebDriverManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
public class sample {

  private WebDriver driver;
  // You can update this list to change the expected symbols
  private final List<String> expectedStockSymbols = Arrays.asList("GOOG", "AAPL", "MSFT", "AMZN", "NVDA", "TSLA", "META", "BRK-B");
  @BeforeClass
  public void setUp() {
    WebDriverManager.chromedriver().setup();
    driver = new ChromeDriver();
    driver.manage().window().maximize();
  }

  @Test
  public void verifyGoogleFinanceStocks() {
    // 1. Opens a webpage www.google.com/finance on a Chrome browser
    driver.get("https://www.google.com/finance");

    // 2. Verifies the page is loaded by asserting the page title
    String pageTitle = driver.getTitle();
    Assert.assertTrue(pageTitle.contains("Google Finance"), "Page title does not contain 'Google Finance'");
    System.out.println("Page title is: " + pageTitle);

    // 3. Retrieves the stock symbols listed under the section “You may be interested in info”
    // hardcoded xpath. else would create separate file and store it.
    List<WebElement> stockElements = driver.findElements(By.xpath("//section[@aria-labelledby='smart-watchlist-title']//div[@id='smart-watchlist-title']/following-sibling::ul//li"));
    List<String> retrievedStockSymbols = new ArrayList<>();

    // Iterator itr = stockElements.iterator();
    // while(itr.hasNext()){
    // WebElement element = (WebElement)itr.next();
    // continue below for loop

    System.out.println("Retrieved the following stock symbols:");
    for (WebElement element : stockElements) {
      try {
        WebElement symbolElement = element.findElement(By.className("COaKTb"));
        String symbol = symbolElement.getText().trim();
        retrievedStockSymbols.add(symbol);
        System.out.println("- " + symbol);
      } catch (Exception e) {
        // Handle cases where a symbol is not found in an element
        System.out.println("An element in 'You may be interested in' did not contain a stock symbol.");
      }
    }

    // Convert to sets for comparison
    Set<String> retrievedSet = retrievedStockSymbols.stream().collect(Collectors.toSet());
    Set<String> expectedSet = expectedStockSymbols.stream().collect(Collectors.toSet());


    // 5. Print all stock symbols that are in (3) but not in expectedStockSymbols
    Set<String> onlyInRetrieved = retrievedSet.stream()
        .filter(s -> !expectedSet.contains(s))
        .collect(Collectors.toSet());

    if (!onlyInRetrieved.isEmpty()) {
      System.out.println("\nStock symbols found on the page but not in the expected list:");
      onlyInRetrieved.forEach(System.out::println);
    } else {
      System.out.println("\nNo stock symbols were found on the page that are not in the expected list.");
    }

    // 6. Print all stock symbols that are in expectedStockSymbols but not in retrieved list
    Set<String> onlyInExpected = expectedSet.stream()
        .filter(s -> !retrievedSet.contains(s))
        .collect(Collectors.toSet());

    if (!onlyInExpected.isEmpty()) {
      System.out.println("\nStock symbols in the expected list but not found on the page:");
      onlyInExpected.forEach(System.out::println);
    } else {
      System.out.println("\nNo stock symbols were in the expected list that were not found on the page.");
    }

    // 4. Compare the stock symbols in (3) with expectedStockSymbols
    Assert.assertTrue(onlyInExpected.isEmpty() && onlyInRetrieved.isEmpty(), "Stock symbol lists do not match.");
  }

  @AfterClass
  public void tearDown() {
    if (driver != null) {
      driver.quit();
    }
  }
}
