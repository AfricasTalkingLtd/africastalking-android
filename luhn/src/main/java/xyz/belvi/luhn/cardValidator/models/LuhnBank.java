package xyz.belvi.luhn.cardValidator.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by aksalj on 27/10/2017.
 */

public final class LuhnBank implements Parcelable {

    private String accountName;
    private String accountNumber;
    private String dateOfBirth;
    private int bankCode;

    public LuhnBank(String accountName, String accountNumber, int bankCode, String dateOfBirth) {
        this.accountName = accountName;
        this.accountNumber = accountNumber;
        this.bankCode = bankCode;
        this.dateOfBirth = dateOfBirth;
    }

    protected LuhnBank(Parcel in) {
        accountName = in.readString();
        accountNumber = in.readString();
        bankCode = in.readInt();
        dateOfBirth = in.readString();
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
        dest.writeInt(bankCode);
        dest.writeString(dateOfBirth);
    }

    public String getAccountName() {
        return accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public int getBankCode() {
        return bankCode;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }
}
