package com.selenium.mini_project;
	
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import java.net.URL;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
	
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
	
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
	
public class DeleteSkillSet {
	public String url = "https://opensource-demo.orangehrmlive.com";
	
	private WebDriver driver;
	private WebDriverWait wait;
	private SoftAssert softAssert;

	// setup
	@Parameters("browser")
    @BeforeClass
    public void setUp(String browser) throws Exception {
        
        String hubUrl = "http://selenium-hub:4444/wd/hub";  // Grid Hub URL

        switch (browser.toLowerCase()) {
            
            case "chrome":
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-dev-shm-usage");  // prevents tab crash
                chromeOptions.addArguments("--disable-gpu");
                chromeOptions.addArguments("--remote-debugging-port=9222");
                driver = new RemoteWebDriver(new URL(hubUrl), chromeOptions);
                break;

            case "edge":
                EdgeOptions edgeOptions = new EdgeOptions();
                edgeOptions.addArguments("--no-sandbox");
                edgeOptions.addArguments("--disable-dev-shm-usage");  // prevents tab crash
                edgeOptions.addArguments("--disable-gpu");
                driver = new RemoteWebDriver(new URL(hubUrl), edgeOptions);
                break;

            case "firefox":
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                driver = new RemoteWebDriver(new URL(hubUrl), firefoxOptions);
                break;

            

            default:
                throw new IllegalArgumentException("Unsupported Browser: " + browser);
        }

        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        softAssert = new SoftAssert();
        driver.get(url);
        driver.manage().deleteAllCookies();
        driver.manage().window().maximize();
        cleanScreenshotsDirectory();
    }

	
	public void login(String username, String password) {
		WebElement usernameInputField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));
		WebElement passwordInputField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("password")));
		WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@type='submit']")));

		// adding hard assertions
		Assert.assertTrue(usernameInputField.isDisplayed(), "Username Field is not displayed");
		Assert.assertTrue(passwordInputField.isDisplayed(), "password field is not displayed");
		Assert.assertTrue(loginButton.isDisplayed(), "Login Button is not displayed");

		usernameInputField.sendKeys(username);
		passwordInputField.sendKeys(password);
		loginButton.click();

		wait.until(ExpectedConditions.urlContains("dashboard"));
		
		Assert.assertTrue(driver.getCurrentUrl().contains("dashboard"), "login failed - username or password invalid");
	}

	public void adminPage() {
		// checking the admin page shows up or not
		WebElement admin = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Admin']")));
		admin.click();

		wait.until(ExpectedConditions.urlContains("admin"));
		softAssert.assertTrue(driver.getCurrentUrl().contains("admin"), "admin page not found");
	}

	public void jobCategoriesPage() {	
		// locating the job tab
		WebElement jobMenu = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[normalize-space()='Job']")));
		jobMenu.click();

		// choosing the 'Job Categories' from the drop down
		WebElement jobCategories = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[text()='Job Categories']")));
		jobCategories.click();

		wait.until(ExpectedConditions.urlContains("jobCategory"));
		
		softAssert.assertTrue(driver.getCurrentUrl().contains("jobCategory"), "Job Categories page not opened");
	}

	public void addCategory(String category) {
		// adding the category
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h6[normalize-space()='Job Categories']")));

		// locating 'add' button
		WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[normalize-space()='Add']")));
		softAssert.assertTrue(addButton.isDisplayed(), "Add button is not displayed");
		addButton.click();
		
		// jib category form must be displayed
		wait.until(ExpectedConditions.urlContains("saveJobCategory"));
		softAssert.assertTrue(driver.getCurrentUrl().contains("saveJobCategory"),	"Add Job Catrgory details form not opened");


		// adding job category
		WebElement jobCategoryInputField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[normalize-space()='Name']/following::input[1]")));
		jobCategoryInputField.sendKeys(category);

		WebElement saveButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@type='submit']")));
		saveButton.click();
	}

	public void deleteCategory(String category) {
		// deleting the category
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h6[normalize-space()='Job Categories']")));
		
		// element to be deleted path
		String path = "//div[text()='" + category + "']";
		WebElement toBeDeleted = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(path)));

		WebElement deleteIcon = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[text()='" + category+ "']/ancestor::div[contains(@class,'oxd-table-row')]/descendant::i[contains(@class, 'bi-trash')]/parent::button")));
		softAssert.assertTrue(deleteIcon.isDisplayed(), "Delete Button Icon not visible");

		// click the delete icon
		deleteIcon.click();

		// pop up for deletion
		WebElement popUp = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'orangehrm-dialog-popup')]")));
		softAssert.assertTrue(popUp.isDisplayed(), "pop up is not displayed");

		WebElement deleteButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()=' Yes, Delete ']")));

		// checking is delete button is displayed
		softAssert.assertTrue(deleteButton.isDisplayed(), "Delete Button not available");
		// clicking the final delete
		deleteButton.click();
	}

	public void takeScreenshot(String fileName) {
		try {
			TakesScreenshot ts = (TakesScreenshot) driver;
			File source = ts.getScreenshotAs(OutputType.FILE);

			// Always create screenshots directory
			File screenshotsDir = new File("screenshots");
			if (!screenshotsDir.exists()) {
				screenshotsDir.mkdir();
			}

			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
			String timestamp = LocalDateTime.now().format(dtf);

			File destination = new File(screenshotsDir, fileName + "_" + timestamp + ".png");

			Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);

			System.out.println("Screenshot saved at: " + destination.getAbsolutePath());

		} catch (Exception e) {
			System.out.println("Screenshot failed: " + e.getMessage());
		}
	}
	public static void cleanScreenshotsDirectory() {
	    File screenshotsDir = new File("screenshots");

	    if (screenshotsDir.exists() && screenshotsDir.isDirectory()) {
	        File[] files = screenshotsDir.listFiles();
	        if (files != null) {
	            for (File file : files) {
	                file.delete();
	            }
	        }
	        System.out.println("🧹 Old screenshots deleted.");
	    }
	}

	// Test 1 – Login
	@Test(priority = 1)
	public void testLogin() {
		driver.navigate().refresh();
		login("Admin", "admin123");
		takeScreenshot("Login Success");
		softAssert.assertAll();
	}

	// Test 2 – Navigate to Admin
	@Test(priority = 2, dependsOnMethods = "testLogin")
	public void testAdminPage() {
		adminPage();
		takeScreenshot("Admin Page accessed");
		softAssert.assertAll();
	}

	// Test 3 - Open Job Dependencies
	@Test(priority = 3, dependsOnMethods = "testAdminPage")
	public void testJobCategoriesPage() {
		jobCategoriesPage();
		takeScreenshot("Job Categories Found");
		softAssert.assertAll();
	}

	@Parameters("browser")
    @Test(priority = 4, dependsOnMethods = "testJobCategoriesPage")
    public void testAddCategory(String browser) {
        switch (browser.toLowerCase()) {
            case "chrome"   -> addCategory("Test_Engineer_chrome");
            case "firefox"  -> addCategory("Test_Engineer_firefox");
            case "edge"     -> addCategory("Test_Engineer_edge");
            default         -> addCategory("Test_Engineer_default");
        }
        takeScreenshot("Category Added");
        softAssert.assertAll();
    }

    @Parameters("browser")
    @Test(priority = 5, dependsOnMethods = "testAddCategory")
    public void testDeleteCategory(String browser) {
        switch (browser.toLowerCase()) {
            case "chrome"   -> deleteCategory("Test_Engineer_chrome");
            case "firefox"  -> deleteCategory("Test_Engineer_firefox");
            case "edge"     -> deleteCategory("Test_Engineer_edge");
            default         -> deleteCategory("Test_Engineer_default");
        }
        takeScreenshot("Category Deleted");
        softAssert.assertAll();
    }


	// tear down
	@AfterClass
	public void tearDown() {
		driver.quit();
	}
}
