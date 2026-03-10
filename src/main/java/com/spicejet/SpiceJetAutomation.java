package com.spicejet;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
/**
 * SpiceJet Flight Booking Automation
 * Automates: One-way booking from Delhi to Bangalore, selecting next date,
 * choosing first available flight, and filling passenger details.
 */
public class SpiceJetAutomation {
    private static final String SPICEJET_URL = "https://www.spicejet.com/";
    private static final int EXPLICIT_WAIT_SECONDS = 30;
    private static final int IMPLICIT_WAIT_SECONDS = 10;
    public static void main(String[] args) {
        WebDriver driver = null;
        try {
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver();
            run(driver);
            System.out.println("Automation completed successfully!");
            Thread.sleep(5000); // Keep browser open to see result
        } catch (Exception e) {
            System.err.println("Automation failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }
    /**
     * Runs the full SpiceJet booking flow. Can be called from main or from tests.
     */
    public static void run(WebDriver driver) throws InterruptedException {
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(IMPLICIT_WAIT_SECONDS));
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(EXPLICIT_WAIT_SECONDS));
        System.out.println("Step 1: Opening SpiceJet website...");
        driver.get(SPICEJET_URL);
        waitForPageLoad(driver);
        System.out.println("Step 2: Selecting One Way travel...");
        selectOneWay(driver, wait);
        System.out.println("Step 3: Selecting Delhi as origin and Bangalore as destination...");
        selectOriginAndDestination(driver, wait);
        System.out.println("Step 4: Selecting next date for travel...");
        selectNextDate(driver, wait);
        System.out.println("Step 5: Clicking Search button...");
        clickSearchButton(driver, wait);
        System.out.println("Step 6: Selecting first available flight...");
        selectFirstFlight(driver, wait);
        System.out.println("Step 7: Filling passenger details (John Doe)...");
        fillPassengerDetails(driver, wait);
    }
    private static void waitForPageLoad(WebDriver driver) {
        try {
            Thread.sleep(3000); // Initial page load
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    private static void selectOneWay(WebDriver driver, WebDriverWait wait) {
        try {
            // Try multiple selectors - SpiceJet may use different structures
            By oneWaySelectors = By.xpath(
                    "//input[@value='OneWay'] | " +
                            "//input[@id='ctl00_mainContent_rbtnl_Trip_0'] | " +
                            "//label[contains(text(),'One Way')] | " +
                            "//div[contains(@class,'trip')]//*[contains(text(),'One Way')]"
            );
            WebElement oneWay = wait.until(ExpectedConditions.elementToBeClickable(oneWaySelectors));
            oneWay.click();
            Thread.sleep(1500); // Wait for UI to update
        } catch (Exception e) {
            System.out.println("One Way might already be selected (default). Continuing...");
        }
    }
    private static void selectOriginAndDestination(WebDriver driver, WebDriverWait wait) {
        // Click on From field to open dropdown
        System.out.println("Step 3: Selecting Delhi as origin and Bangalore as destination...");



// Initialize the Actions class to simulate keyboard strokes
        Actions actions = new Actions(driver);

// 1. Click the main Origin container to open the dropdown and activate the cursor
       // WebElement originContainer = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[text()='From']")));
        //originContainer.click();

// 2. Type "Delhi" directly into whatever field just became active
        //actions.sendKeys("Delhi").perform();

// 3. Wait for the auto-suggest dropdown and click Delhi (DEL)
       // WebElement originCity = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[text()='DEL']")));
       // originCity.click();

// 4. Click the Destination container
        WebElement destContainer = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@data-testid='to-testID-destination']")));
        destContainer.click();

// 5. Type "Bengaluru" into the active destination field
        actions.sendKeys("Bengaluru").perform();

// 6. Wait for the auto-suggest dropdown and click Bangalore (BLR)
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        WebElement destCity = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[contains(text(),'Bengaluru')]")));
        destCity.click();
    }
    private static void selectNextDate(WebDriver driver, WebDriverWait wait) {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        int day = tomorrow.getDayOfMonth();
        // SpiceJet calendar uses full month names (e.g., "March", "April")
        String month = tomorrow.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        String year = String.valueOf(tomorrow.getYear());
        // Click date picker to open calendar
        By datePickerTrigger = By.xpath(
                "//input[@id='ctl00_mainContent_view_date1'] | " +
                        "//button[contains(@class,'ui-datepicker-trigger')] | " +
                        "//*[contains(@id,'view_date')] | " +
                        "//div[contains(@data-testid,'departure')]"
        );
        WebElement dateTrigger = wait.until(ExpectedConditions.elementToBeClickable(datePickerTrigger));
        dateTrigger.click();
        new WebDriverWait(driver, Duration.ofSeconds(1))
                .until(d -> false);
        // Navigate to correct month/year if needed
        String currentMonth = "";
        String currentYear = "";
        try {
            WebElement monthEl = driver.findElement(By.xpath("(//span[@class='ui-datepicker-month'])[1]"));
            WebElement yearEl = driver.findElement(By.xpath("(//span[@class='ui-datepicker-year'])[1]"));
            currentMonth = monthEl.getText().trim();
            currentYear = yearEl.getText().trim();
        } catch (Exception ignored) {}
        // Click Next until we reach the correct month/year
        while (!currentMonth.equalsIgnoreCase(month) || !currentYear.equals(year)) {
            try {
                WebElement nextBtn = driver.findElement(By.xpath("//span[text()='Next'] | //a[contains(@class,'next')]"));
                nextBtn.click();
                Thread.sleep(800);
                WebElement monthEl = driver.findElement(By.xpath("(//span[@class='ui-datepicker-month'])[1]"));
                WebElement yearEl = driver.findElement(By.xpath("(//span[@class='ui-datepicker-year'])[1]"));
                currentMonth = monthEl.getText().trim();
                currentYear = yearEl.getText().trim();
            } catch (Exception e) {
                break;
            }
        }
        // Click the date
        List<WebElement> dates = driver.findElements(By.xpath("//a[@class='ui-state-default']"));
        for (WebElement dateEl : dates) {
            if (dateEl.getText().trim().equals(String.valueOf(day))) {
                dateEl.click();
                break;
            }
        }
        new WebDriverWait(driver, Duration.ofSeconds(1))
                .until(d -> false);
    }
    private static void clickSearchButton(WebDriver driver, WebDriverWait wait) {
        By searchButton = By.xpath(
                "//input[@id='ctl00_mainContent_btn_FindFlights'] | " +
                        "//input[@value='Search'] | " +
                        "//div[contains(@class,'search')]//input[@type='submit'] | " +
                        "//*[contains(text(),'Search') and (self::button or self::input)]"
        );
        WebElement searchBtn = wait.until(ExpectedConditions.elementToBeClickable(searchButton));
        searchBtn.click();
        new WebDriverWait(driver, Duration.ofSeconds(8))
                .until(d -> false); // Wait for flight results to load
    }
    private static void selectFirstFlight(WebDriver driver, WebDriverWait wait) {
        try {
            // Common selectors for flight selection - first book/select button
            By firstFlightSelectors = By.xpath(
                    "(//input[contains(@id,'Button') and contains(@value,'Book')])[1] | " +
                            "(//div[contains(@class,'flight')]//button[contains(text(),'Book')])[1] | " +
                            "(//div[contains(@class,'flight')]//input[@type='radio'])[1] | " +
                            "(//*[contains(text(),'Book') or contains(text(),'Select')])[1] | " +
                            "(//div[contains(@data-testid,'flight')]//button)[1]"
            );
            WebElement firstFlight = wait.until(ExpectedConditions.elementToBeClickable(firstFlightSelectors));
            firstFlight.click();
            Thread.sleep(5000); // Wait for passenger form to load
        } catch (Exception e) {
            System.out.println("Trying alternative flight selector...");
            try {
                List<WebElement> flightButtons = driver.findElements(By.xpath("//input[contains(@value,'Book')] | //button[contains(text(),'Book')]"));
                if (!flightButtons.isEmpty()) {
                    flightButtons.get(0).click();
                    Thread.sleep(5000);
                }
            } catch (Exception ex) {
                throw new RuntimeException("Could not select first flight. Page structure may have changed.", ex);
            }
        }
    }
    private static void fillPassengerDetails(WebDriver driver, WebDriverWait wait) {
        try {
            // First Name - John
            By firstNameSelectors = By.xpath(
                    "//input[contains(@id,'FirstName')] | " +
                            "//input[contains(@name,'firstName')] | " +
                            "//input[@placeholder='First Name'] | " +
                            "//input[contains(@data-testid,'first')]"
            );
            WebElement firstName = wait.until(ExpectedConditions.visibilityOfElementLocated(firstNameSelectors));
            firstName.clear();
            firstName.sendKeys("John");
            // Last Name - Doe
            By lastNameSelectors = By.xpath(
                    "//input[contains(@id,'LastName')] | " +
                            "//input[contains(@name,'lastName')] | " +
                            "//input[@placeholder='Last Name'] | " +
                            "//input[contains(@data-testid,'last')]"
            );
            WebElement lastName = wait.until(ExpectedConditions.visibilityOfElementLocated(lastNameSelectors));
            lastName.clear();
            lastName.sendKeys("Doe");
            Thread.sleep(2000);
        } catch (Exception e) {
            System.out.println("Passenger detail fields may have different structure. Error: " + e.getMessage());
            // Try by index for first passenger
            try {
                List<WebElement> nameInputs = driver.findElements(By.xpath("//input[contains(@type,'text') and (contains(@id,'Name') or contains(@name,'Name'))]"));
                if (nameInputs.size() >= 2) {
                    nameInputs.get(0).clear();
                    nameInputs.get(0).sendKeys("John");
                    nameInputs.get(1).clear();
                    nameInputs.get(1).sendKeys("Doe");
                }
            } catch (Exception ex) {
                throw new RuntimeException("Could not fill passenger details.", ex);
            }
        }
    }
}

