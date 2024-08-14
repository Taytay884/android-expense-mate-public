# Group Expenses App - Expense Mate

![ic_launcher](https://github.com/user-attachments/assets/65322c20-2009-4108-8b25-42bbb613013c)

A simple and efficient Android app designed to help groups track and manage shared expenses. Perfect for trips, events, or any situation where people need to split costs and settle up easily.

## How it works

1. **Create a Group**: Add a group name and select participants.
2. **Add Expenses**: Input expenses with details about the payer and amount. The app will evenly split the cost among participants.
3. **View Balances**: Check the balance tab to see who owes money and how much.
4. **Settle Up**: Once participants pay each other back, use the settle-up feature to track and update balances.

## Example Use Case: Weekend Trip

- **Group Members**: Shimrit, Josh, Yoram, Nahum, and Itay.
- **Expenses**: Gas, meals, cabin rental, and groceries are added, and the costs are split equally among the group.
- **Balances**: The app tracks how much each person owes or is owed.
- **Settle-Up**: Participants can easily settle their debts, and the app updates balances accordingly.

## Idea 

![image](https://github.com/user-attachments/assets/1175eea9-abb7-4af4-b1ed-d073fb70164f)

## Demo - showing the weekend use case

https://github.com/user-attachments/assets/6777fc97-016c-42d8-985d-d330686c45ce

## Technologies

- Java
- Firebase Authentication
- Firebase Firestore (database)

## Features

- **User Authentication**: Register and log in to the app.
- **Group Management**: Create groups with registered participants.
- **Expense Tracking**: Add and remove expenses with payer, amount, and description.
- **Settle-Up**: Add and remove settle-up transactions to balance group costs.
- **Automatic Balance Calculation**: Split expenses among participants and calculate balances.

## Known issues

- logout/login and then closing and opening the app can crash - On MainActivity the logic of understanding the current user made a lot of trouble for me.
- MVC is not implemented well enough. I tried hard at start but it was very difficult so I just hack it and afterwards when I got so experience, on GroupActivity I tried to implement Controller and Manager.
- No shared components, it was really hard for me to create something shared.
- All the groups are fetched no matter which user you're, when you try to enter a group you're not belong to, it crashes. (Just not enough time)
- I think I took too complicated app idea - I invested a lot of time on this project.
