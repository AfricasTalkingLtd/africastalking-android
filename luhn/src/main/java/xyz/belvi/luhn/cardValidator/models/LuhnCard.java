package xyz.belvi.luhn.cardValidator.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zone2 on 7/5/17.
 */

public final class LuhnCard implements Parcelable {
    private String pan, cardName, expDate, cvv, pin, countryCode;
    private int expMonth, expYear;


    public LuhnCard(String pan, String cardName, String expDate, String cvv, String pin, int expMonth, int expYear, String countryCode) {
        this.pan = pan;
        this.cardName = cardName;
        this.expMonth = expMonth;
        this.expYear = expYear;
        this.cvv = cvv;
        this.pin = pin;
        this.expDate = expDate;
        this.countryCode = countryCode;
    }

    protected LuhnCard(Parcel in) {
        pan = in.readString();
        cardName = in.readString();
        expMonth = in.readInt();
        expYear = in.readInt();
        cvv = in.readString();
        pin = in.readString();
        expDate = in.readString();
        countryCode = in.readString();
    }

    public static final Creator<LuhnCard> CREATOR = new Creator<LuhnCard>() {
        @Override
        public LuhnCard createFromParcel(Parcel in) {
            return new LuhnCard(in);
        }

        @Override
        public LuhnCard[] newArray(int size) {
            return new LuhnCard[size];
        }
    };

    @Override
    public final int describeContents() {
        return 0;
    }

    @Override
    public final void writeToParcel(Parcel dest, int flags) {
        dest.writeString(pan);
        dest.writeString(cardName);
        dest.writeInt(expMonth);
        dest.writeInt(expYear);
        dest.writeString(cvv);
        dest.writeString(pin);
        dest.writeString(expDate);
        dest.writeString(countryCode);
    }

    public String getPan() {
        return this.pan;
    }

    public int getExpMonth() {
        return this.expMonth;
    }

    public String getExpMonthShort() {
        switch (this.expMonth) {
            case 1:
                return "Jan";
            case 2:
                return "Feb";
            case 3:
                return "Mar";
            case 4:
                return "Apr";
            case 5:
                return "May";
            case 6:
                return "Jun";
            case 7:
                return "Jul";
            case 8:
                return "Aug";
            case 9:
                return "Sep";
            case 10:
                return "Oct";
            case 11:
                return "Nov";
            case 12:
            default:
                return "Dec";
        }
    }

    public int getExpYear() {
        return this.expYear;
    }

    public String getExpDate() {
        return expDate;
    }

    public String getCvv() {
        return cvv;
    }

    public String getPin() {
        return pin;
    }

    public String getCardName() {
        return this.cardName;
    }

    public String getCountryCode() {
        return countryCode;
    }
}
