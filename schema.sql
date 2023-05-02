CREATE TABLE Memb (Email varchar(255) not null primary key, Password varchar(255) not null, FullName varchar(255), ScreenName varchar(255) not null, DateOfBirth date not null, Gender varchar(255), Status varchar(1024), Loc varchar(255), Visibility integer not null);
CREATE TABLE Friendship (MemberA varchar(255) not null, MemberB varchar(255) not null, StartDate date not null,
    PRIMARY KEY (MemberA, MemberB),
    FOREIGN KEY (MemberA) REFERENCES Memb(Email) ON DELETE CASCADE,
    FOREIGN KEY (MemberB) REFERENCES Memb(Email) ON DELETE CASCADE);
CREATE TABLE Post (PostID integer generated always as identity primary key, authorID varchar(255) not null, postbody varchar(1024) not null, timestamp date not null, parentID integer,
    FOREIGN KEY (ParentID) REFERENCES Post(PostID) ON DELETE CASCADE,
    FOREIGN KEY (authorID) REFERENCES Memb(Email) ON DELETE CASCADE);
CREATE TABLE PostLike (PostID integer not null, MemberID varchar(255) not null, 
    PRIMARY KEY (PostID, MemberID),
    FOREIGN KEY (PostID) REFERENCES Post(PostID) ON DELETE CASCADE,
    FOREIGN KEY (MemberID) REFERENCES Memb(Email) ON DELETE CASCADE);
CREATE TABLE FriendRequest (Requestor varchar(255) not null, Requestee varchar(255) not null, StartDate date not null,
    PRIMARY KEY (Requestor, Requestee),
    FOREIGN KEY (Requestor) REFERENCES Memb(Email) ON DELETE CASCADE,
    FOREIGN KEY (Requestee) REFERENCES Memb(Email) ON DELETE CASCADE);
