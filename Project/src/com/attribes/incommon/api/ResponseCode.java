package com.attribes.incommon.api;

/**
 * Created by ashoaib on 7/6/13.
 *
 * This enum will contain all the codes either being sent by Kabuto server or being used by application for
 * error handling.
 *
 * Make sure that we have string entries available in the Strings.xml file against all enumerations below.
 */
public enum ResponseCode {
    /**
     * Format for consistency:
     * EnumName,        //String resource Id
     *
     * Example:
     * InvalidEmailAddress,     //R.string.
     *
     */
    InvalidEmailAddress,
    AuthenticationFailed,
    NoSuchCollaboration,
    Ok,
    NetworkFailure,
    UnknownHost,
    NoRemainingSuperusers,
    SocketTimeout,
    NoCollaborationAccess,
    ConnectionTimeout,
    InvitationDoesNotExist,
    NoDocumentAccess,
    NoSuchDocument,
    NoSuchPost,
    NetworkResponseNull,    //Our own check
    ImageLoadFailure,       //Our own check
    NoPendingRequest,
    ApplicationException,
    InternalError,
    PendingUserRegistration,
    CollaborationUpdateError,
    UnspecifiedError,
    NoSuchNotification,
    NoDocumentWrite,
    MissingParameter,
    MismatchedSerial;
}
