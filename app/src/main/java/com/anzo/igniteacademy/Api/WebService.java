package com.anzo.igniteacademy.Api;

public class WebService {

    public static final int TIMEOUT_LIMIT = 120000;

    public static final String API_KEY = "ignite@121";

    public static final int SUCCESS = 1;
    public static final int FAILED = 0;

    public static final String domain = "https://ignite.management/apis";

    public static final String LOGIN = domain + "/users/login";
    public static final String TOTAL_RECORD = domain + "/dashboard/totals";
    public static final String GET_COURSE = domain + "/course/get";
    public static final String GET_COUNTRY = domain + "/country/get";

    //Enquiry
    public static final String ENQUIRY_LIST = domain + "/enquiry/get";
    public static final String ADD_ENQUIRY = domain + "/enquiry/add_edit";
    public static final String ENQUIRY_DETAILS = domain + "/enquiry/details";
    public static final String DELETE_ENQUIRY = domain + "/enquiry/delete";
    public static final String GET_BATCH_TIME = domain + "/enquiry/get_batch_time";
    public static final String FEES_PAYMENT = domain + "/enquiry/fees_payment";
    public static final String GET_DOCUMENT = domain + "/enquiry/get_documents";
    public static final String UPLOAD_PHOTO = domain + "/enquiry/upload_photo";
    public static final String UPLOAD_DOCUMENT = domain + "/enquiry/upload_documents";
    public static final String DELETE_DOCUMENT = domain + "/enquiry/delete_documents";
    public static final String ENQUIRY_ENROLLMENT = domain + "/enquiry/enrollment";
    public static final String CANCEL_ENQUIRY =domain +"/enquiry/cancel";
    public static final String Fee_Receipt = domain + "/enquiry/fees_receipts";

    //FOLLOWUP
    public static final String FOLLOW_UP_LIST = domain + "/follow_up/get";
    public static final String ADD_FOLLOW_UP = domain + "/follow_up/add_edit";
    public static final String DELETE_FOLLOW_UP = domain + "/follow_up/delete";

    //Notification
    public static final String GET_NOTIFICATION = domain + "/notification/get";

    public static class Params {
        public static String STATUS = "status";
        public static String MESSAGE = "message";
        public static String DATA = "data";
    }
}
