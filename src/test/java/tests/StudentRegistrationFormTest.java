package tests;

import org.junit.jupiter.api.Test;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.github.javafaker.Faker;

import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;

public class StudentRegistrationFormTest {

    @Test
    void dataAppearsInModalPopUpWindow() {
        Faker faker = new Faker();

        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        String email = firstName.toLowerCase() + "." + faker.letterify("??????").toLowerCase() + faker.numerify("###") + "." + "@gmail.com";
        int g = faker.number().numberBetween(1, 2);
        String gender = (faker.number().numberBetween(1, 2) > 1 ? "Male" : "Female");
        String phone =  faker.numerify("##########");
        String day = Integer.toString(faker.number().numberBetween(1, 28));
        String month = Integer.toString(faker.number().numberBetween(0,11));
        String year = Integer.toString(faker.number().numberBetween(1950, 2005));
        int subject1 = faker.number().numberBetween(0, 4);
        int subject2 = faker.number().numberBetween(0, 4);
        int hobbyNumber = faker.number().numberBetween(1, 3);
        String address = faker.address().fullAddress();
        int stateNumber = faker.number().numberBetween(0, 3);
        String photoFileName = (g>1 ? "award1_700.jpg" : "1518521058110646316.jpg");

        open("https://demoqa.com/automation-practice-form");
        $(".main-header").shouldHave(text("Practice Form")).should(Condition.appear);

        $("#firstName").setValue(firstName);
        $("#lastName").setValue(lastName);
        $("#userEmail").setValue(email);
        //Gender
        $$("#genterWrapper label").findBy(text(gender)).click();
        //Phone
        $("#userNumber").setValue(phone);
        //Date of Birth
        $("#dateOfBirthInput").click();
        $(".react-datepicker__month-select").click();
        $(".react-datepicker__month-select [value='" + month + "']").click();
        $(".react-datepicker__year-select").click();
        $(".react-datepicker__year-select [value='" + year + "']").click();
        String selectedMonthYear = $(".react-datepicker__current-month--hasMonthDropdown").getText();
        String prefilledDay = (day.length()>1 ? day : "0" + day);
        $(".react-datepicker__day--0" + prefilledDay + ":not(.react-datepicker__day--outside-month)").click();
        //Subject
        String selectedSubject = selectSubject(subject1, subject2);
        //Hobby
        String selectedHobby = selectHobby(hobbyNumber);
        //Picture
        $("#uploadPicture").uploadFromClasspath("img/" + photoFileName);
        //Address
        $("#currentAddress").scrollTo().setValue(address);
        String selectedStateAndCity = selectRandomItemInDropDownList(stateNumber);

        //Submit form
        $("#submit").scrollTo().click();

        //Verifications
        $(".modal-content").should(appear);
        ElementsCollection form = $$(".modal-body tr");
        form.filterBy(text("Student Name")).last().shouldHave(text(firstName + " " + lastName));
        form.filterBy(text("Student Email")).last().shouldHave(text(email));
        form.filterBy(text("Gender")).last().shouldHave(text(gender));
        form.filterBy(text("Mobile")).last().shouldHave(text(phone));
        form.filterBy(text("Date of Birth")).last().shouldHave(text(prefilledDay + " " + selectedMonthYear.replace(" ",",")));
        form.filterBy(text("Subjects")).last().shouldHave(text(selectedSubject));
        form.filterBy(text("Hobbies")).last().shouldHave(text(selectedHobby));
        form.filterBy(text("Address")).last().shouldHave(text(address));
        form.filterBy(text("State and City")).last().shouldHave(text(selectedStateAndCity));
        form.filterBy(text("Picture")).last().shouldHave(text(photoFileName));

        $("#closeLargeModal").scrollTo().click();
    }

    private String selectHobby(int i) {
        String locator = "[for='hobbies-checkbox-" + i + "']";
        $(locator).click();
        String s = $(locator).getText();
        if (i>1) {
            String fl = "[for='hobbies-checkbox-1']";
            $(fl).click();
            s = s + ", " + $(fl).getText();
        }
        return s;
    }

    private String selectSubject(int n1, int n2) {
        String c = "abcde";
        String c2 = "imnlo";

        $("#subjectsInput").setValue(String.valueOf(c.charAt(n1)));
        $(".subjects-auto-complete__menu-list").should(appear);
        $$(".subjects-auto-complete__menu-list").get(0).click();
        String s = $(".subjects-auto-complete__multi-value__label").getText();

        $("#subjectsInput").setValue(String.valueOf(c2.charAt(n2)));
        $(".subjects-auto-complete__menu-list").should(appear);
        $$(".subjects-auto-complete__menu-list").last().click();
        return s + ", " + $$(".subjects-auto-complete__multi-value__label").last().getText();
    }

    private String selectRandomItemInDropDownList(int stateNumber) {
        $("#state div[class $= '-placeholder']").scrollTo().click();
        $("div[class $= '-menu']").should(appear);
        int amount = $$("div[class $= '-menu'] div div").size() - 1;
        if(amount <= stateNumber)
            $("#react-select-3-option-" + amount).click();
        else
            $("#react-select-3-option-" + stateNumber).click();

        $("#city div[class $= '-placeholder']").scrollTo().click();
        $("div[class $= '-menu']").should(appear);
        $$("div[class $= '-menu'] div div").last().click();
        return $("#state [class $= '-singleValue']").getText() + " " + $("#city [class $= '-singleValue']").getText();
    }
}
