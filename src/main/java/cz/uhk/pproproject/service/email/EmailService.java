package cz.uhk.pproproject.service.email;
public interface EmailService {
    //return true if email is sent, false if exception
    boolean sendSimpleMail(EmailDetails details);

    boolean sendMailWithAttachment(EmailDetails details);
}