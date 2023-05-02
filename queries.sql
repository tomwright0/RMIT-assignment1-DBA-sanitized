
-- Get User Feed:
-- Given the single parameter :curUser which is the logged-in user's
-- email, this query will select every post that is either a top-level
-- post by the user or a friend, or is a reply to one of those posts.
SELECT PostID, PostBody, ScreenName, ParentID,
    (SELECT COUNT(*) FROM PostLike WHERE Post.PostID = PostLike.PostID) AS PostLikes,
    (SELECT COUNT(*) FROM PostLike WHERE Post.PostID = PostLike.PostID AND AuthorID = :curUser) AS UserLike
FROM Post JOIN Memb ON AuthorID = Email
WHERE (ParentID IS NULL AND (AuthorID = :curUser
        OR EXISTS(SELECT * FROM Friendship
            WHERE (MemberA = :curUser AND MemberB = AuthorID)
            OR (MemberA = AuthorID AND MemberB = :curUser))))
    OR (ParentID IN (SELECT PostID FROM Post WHERE ParentID IS NULL AND (AuthorID = :curUser
        OR EXISTS(SELECT * FROM Friendship
            WHERE (MemberA = :curUser AND MemberB = AuthorID)
            OR (MemberA = AuthorID AND MemberB = :curUser)))))
ORDER BY Timestamp ASC;

-- Get pending friend requests for user
SELECT Email, ScreenName
FROM FriendRequest JOIN Memb ON Requestor = Email
WHERE Requestee = :curUser
ORDER BY StartDate DESC;

-- Search for users in friend management
SELECT Email, ScreenName,
    (SELECT COUNT(*) FROM Friendship WHERE (MemberA = :curUser AND MemberB = Email)
        OR (MemberA = Email AND MemberB = :curUser)) AS CurrentlyFriends
FROM Memb
WHERE UPPER(ScreenName) LIKE CONCAT('%', CONCAT(UPPER(:searchTerm), '%'))
AND Email != :curUser
ORDER BY CurrentlyFriends DESC, ScreenName ASC;

-- Send/Accept Friend Request
-- The one endpoint on the server will do either one of these things,
-- depending on whether the target has already sent a friend request
-- to the current user.  Java will branch based on the result of this
-- select:
SELECT COUNT(*) FROM FriendRequest WHERE Requestor = :target AND Requestee = :curUser;
-- If the request does not exist, make the reverse request:
    INSERT INTO FriendRequest (Requestor, Requestee, StartDate)
        VALUES (:curUser, :target, SYSDATE);
-- If the request does exist, accept it:
    DELETE FROM FriendRequest WHERE (Requestor = :target AND Requestee = :curUser)
        OR (Requestor = :curUser AND Requestee = :target);
    INSERT INTO Friendship (MemberA, MemberB, StartDate)
        VALUES (:curUser, :target, SYSDATE);

-- Decline/Cancel Friend Request
DELETE FROM FriendRequest WHERE (Requestor = :target AND Requestee = :curUser)
    OR (Requestor = :curUser AND Requestee = :target);

-- Like Post
INSERT INTO PostLike (PostID, MemberID) VALUES (:post, :curUser);

-- Unlike Post
DELETE FROM PostLike WHERE PostID = :post AND MemberID = :curUser;

-- Register User
INSERT INTO Memb (Email, FullName, ScreenName, DateOfBirth, Gender, Password, Visibility, Loc)
    VALUES (:email, :FullName, :ScreenName, :DateOfBirth, :Gender, :encryptedpass, :Visibility, :Location)

-- Login User
SELECT * FROM Memb where email = :email and password = :encryptedpass

-- Update Profile
-- Select is for getting the existing profile details to render the page
SELECT ScreenName, Status, Loc, Visibility FROM Memb WHERE Email = :curUser;
UPDATE Memb
SET ScreenName = :ScreenName, Status = :Status, Loc = :Location, Visibility = :Visibility
WHERE Email = :curUser;

-- Delete Account
DELETE FROM Memb WHERE Email = :curUser;

-- New Post
INSERT INTO Post (AuthorID, PostBody, Timestamp)
     VALUES (?, ?, ?)

-- New Reply
INSERT INTO Post (AuthorID, PostBody, ParentID, Timestamp)
     VALUES (?, ?, ?, ?)