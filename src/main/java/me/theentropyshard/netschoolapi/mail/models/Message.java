/*      NetSchoolAPI. A simple API client for NetSchool by irTech
 *      Copyright (C) 2022 TheEntropyShard
 *
 *      This program is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      (at your option) any later version.
 *
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public License
 *      along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.theentropyshard.netschoolapi.mail.models;

public class Message {
    public String sender;
    public String receiver;
    public String sentDate;
    public String subject;
    public String text;

    /**
     * Создает пустой объект Message
     */
    public Message() {

    }

    /**
     * Создает объект Message инициализируя необходимы для отправки параметры
     *
     * @param receiver Получатель (адресат)
     * @param subject  Тема письма
     * @param text     Текст письма
     */
    public Message(String receiver, String subject, String text) {
        this.receiver = receiver;
        this.subject = subject;
        this.text = text;
    }

    /**
     * Создает объект Message инициализируя все параметры
     *
     * @param sender   Отправитель
     * @param receiver Получатель
     * @param sentDate Дата отправки
     * @param subject  Тема письма
     * @param text     Текст письма
     */
    public Message(String sender, String receiver, String sentDate, String subject, String text) {
        this.sender = sender;
        this.receiver = receiver;
        this.sentDate = sentDate;
        this.subject = subject;
        this.text = text;
    }

    @Override
    public String toString() {
        return "Message{" +
                "sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", sentDate='" + sentDate + '\'' +
                ", subject='" + subject + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
