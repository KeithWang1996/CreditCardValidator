import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;

import java.util.function.UnaryOperator;

public class Controller {

    private int cvvSize = 3;

    Image visa;
    Image mc;
    Image jcb;
    Image amex;
    Image empty;

    @FXML
    protected TextField ccField;

    @FXML
    protected TextField dateField;

    @FXML
    protected TextField cvvField;

    @FXML
    protected ImageView ccImage;

    @FXML
    protected ImageView checkImage;

    public void initialize()
    {
        //format all three input fields
        //Credit card
        UnaryOperator<TextFormatter.Change> creditCardChange = change -> {
            String input = change.getText();
            if((input.matches("[0-9]") && ccField.getText().length() < 16)
                    || change.isDeleted())
            {
                return change;
            }
            return null;
        };
        ccField.setTextFormatter(new TextFormatter<String>(creditCardChange));

        //Date
        UnaryOperator<TextFormatter.Change> dateChange = change -> {
            String input = change.getText();
            if(((input.matches("([0-9])") || input.matches("/")) && dateField.getText().length() < 7)
                    || change.isDeleted())
            {
                return change;
            }
            return null;
        };
        dateField.setTextFormatter(new TextFormatter<String>(dateChange));

        //CVV
        UnaryOperator<TextFormatter.Change> CVVChange = change -> {
            String input = change.getText();
            if(((input.matches("([0-9])")) && cvvField.getText().length() < cvvSize)
                    || change.isDeleted())
            {
                return change;
            }
            return null;
        };
        cvvField.setTextFormatter(new TextFormatter<String>(CVVChange));

        visa = new Image("/logos/visa.png");
        mc = new Image("/logos/mc.png");
        jcb = new Image("/logos/jcb.png");
        amex = new Image("/logos/amex.png");
        empty = new Image("/logos/empty.png");
    }

    @FXML
    protected void onCCFieldTextChanged(KeyEvent key)
    {
        boolean headCheck = false;
        cvvSize = 3;
        cvvField.setPromptText("XXX");
        String ccText = ccField.getText();
        if(ccText.length() < 16 && key.getCharacter().matches("\\d"))
        {
            ccText += key.getCharacter();
        }

        if(ccText.length() >= 1)
        {
            if(ccText.matches("4.*"))
            {
                ccImage.setImage(visa);
                headCheck = true;
            }
            else if(ccText.matches("5.*"))
            {
                ccImage.setImage(mc);
                headCheck = true;
            }
        }

        if(ccText.length() >= 2)
        {
            if(ccText.matches("34.*") || ccText.matches("37.*"))
            {
                ccImage.setImage(amex);
                cvvSize = 4;
                cvvField.setPromptText("XXXX");
                headCheck = true;
            }
            else if (ccText.matches("35.*")) {
                ccImage.setImage(jcb);
                headCheck = true;
            }
        }

        if(!headCheck)
        {
            ccImage.setImage(empty);
            checkImage.setVisible(false);
            return;
        }

        if(LuhnCheck(ccText))
        {
            checkImage.setVisible(true);
        }
        else
        {
            checkImage.setVisible(false);
        }
    }

    private static boolean LuhnCheck(String cardNumberStr)
    {
        if(cardNumberStr.length() != 16)
        {
            return false;
        }

        int sum = 0;
        for(int i = 0; i < 16; ++i)
        {
            char digitChar = cardNumberStr.charAt(i);
            //Convert digit to int
            int digit = digitChar - 48;
            if(i % 2 == 0)//double
            {
                digit *= 2;
                if(digit > 9)
                {
                    digit -= 9;
                }
            }
            sum += digit;
        }

        if(sum % 10 == 0)
        {
            return true;
        }
        return false;
    }
}
