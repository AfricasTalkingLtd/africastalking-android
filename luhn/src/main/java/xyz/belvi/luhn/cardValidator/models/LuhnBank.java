package xyz.belvi.luhn.cardValidator.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by aksalj on 27/10/2017.
 */

public final class LuhnBank implements Parcelable {

    private String accountName;
    private String accountNumber;
    private String bankName;
    private int bankCode;
    private String countryCode;

    public LuhnBank(String accountName, String accountNumber, String bankName, int bankCode, String countryCode) {
        this.accountName = accountName;
        this.accountNumber = accountNumber;
        this.bankName = bankName;
        this.bankCode = bankCode;
        this.countryCode = countryCode;
    }

    protected LuhnBank(Parcel in) {
        accountName = in.readString();
        accountNumber = in.readString();
        bankName = in.readString();
        bankCode = in.readInt();
        countryCode = in.readString();
    }

    public static final Creator<LuhnBank> CREATOR = new Creator<LuhnBank>() {
        @Override
        public LuhnBank createFromParcel(Parcel in) {
            return new LuhnBank(in);
        }

        @Override
        public LuhnBank[] newArray(int size) {
            return new LuhnBank[size];
        }
    };

    @Override
    public final int describeContents() {
        return 0;
    }

    @Override
    public final void writeToParcel(Parcel dest, int flags) {
        dest.writeString(accountName);
        dest.writeString(accountNumber);
        dest.writeString(bankName);
        dest.writeInt(bankCode);
        dest.writeString(countryCode);
    }

    public String getAccountName() {
        return accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public int getBankCode() {
        return bankCode;
    }

    public String getCountryCode() {
        return countryCode;
    }
}
