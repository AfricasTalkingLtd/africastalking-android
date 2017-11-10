package com.africastalking.models.payment.checkout;

/**
 * Created by aksalj on 10/11/2017.
 */

public enum BankCode {
    FCMB_NG(234001),
    Zenith_NG(234002),
    Access_NG(234003),
    GTBank_NG(234004),
    Ecobank_NG(234005),
    Diamond_NG(234006),
    Providus_NG(234007),
    Unity_NG(234008),
    Stanbic_NG(234009),
    Sterling_NG(234010),
    Parkway_NG(234011),
    Afribank_NG(234012),
    Enterprise_NG(234013),
    Fidelity_NG(234014),
    Heritage_NG(234015),
    Keystone_NG(234016),
    Skye_NG(234017),
    Stanchart_NG(234018),
    Union_NG(234019),
    Uba_NG(234020),
    Wema_NG(234021),
    First_NG(234022),
    CBA_KE(254001),
    UNKNOWN(-1);

    final int code;

    BankCode(int code) {
        this.code = code;
    }


    public static BankCode valueOf(int code) {
        BankCode[] codes = values();
        for(BankCode c:codes) {
            if (c.code == code) {
                return c;
            }
        }
        return UNKNOWN;
    }
}
