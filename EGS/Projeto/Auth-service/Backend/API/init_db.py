import sqlite3

connection = sqlite3.connect('database.db')
print("Opened database successfully")

connection.execute('CREATE TABLE users (username TEXT, password TEXT, email TEXT,google_id TEXT,github_id TEXT)')
print("Table created successfully")


connection.close()