-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Nov 16, 2026 at 05:03 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `codelearn`
--

-- --------------------------------------------------------

--
-- Table structure for table `case_studies`
--

CREATE TABLE `case_studies` (
  `id` int(11) NOT NULL,
  `course_id` int(11) NOT NULL,
  `title` varchar(150) NOT NULL,
  `scenario` text NOT NULL,
  `task_description` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `case_studies`
--

INSERT INTO `case_studies` (`id`, `course_id`, `title`, `scenario`, `task_description`) VALUES
(1, 1, 'Python Student Grade Manager', 'A university club needs a Python program to manage student scores. The program should store student names and scores, calculate averages, and determine whether each student passes or fails.', '• Create a menu to add student names and scores.\n• Store the data using an appropriate Python collection.\n• Calculate the average score and pass or fail status.\n• Handle at least two edge cases such as invalid scores or an empty student list.\n• Prepare your code and a short explanation.'),
(2, 2, 'C Book Inventory System', 'A small bookstore needs a C console program to manage book inventory. Each book has a title, stock quantity, and price.', '• Create a structure for storing book information.\n• Use arrays and functions to add, display, and search for books.\n• Allow stock quantities to be updated.\n• Handle unavailable books and invalid stock input.\n• Prepare your source code and a short explanation.'),
(3, 3, 'Java Campus Transportation', 'A campus transportation service needs a Java program to manage vehicles and passenger bookings.', '• Create suitable Java classes for vehicles and bookings.\n• Use constructors and methods appropriately.\n• Allow users to create and display bookings.\n• Handle invalid passenger counts and unavailable vehicles.\n• Prepare your source code and explanation.'),
(4, 4, 'JavaScript Event Participant Manager', 'A student organization needs a JavaScript application to manage participants for campus events.', '• Add participant names and event information.\n• Store participant data using arrays.\n• Create functions to search and remove participants.\n• Handle duplicate participants and empty input.\n• Prepare your code and a short explanation.'),
(5, 5, 'Data Structures Library System', 'A campus library wants a program to manage its books. Students can borrow up to three books, return them, and see which books are overdue.', '• Choose suitable data structures and explain your choices.\n• Implement borrowing and returning books.\n• Handle unavailable books and borrowing limits.\n• Explain the efficiency of the important operations.\n• Prepare your code and explanation for mentor review.'),
(6, 6, 'OOP Student Management System', 'A university needs a student management system that supports different types of students and their academic information.', '• Design suitable classes and objects.\n• Apply encapsulation to important data.\n• Use inheritance for different student types.\n• Demonstrate polymorphism or abstraction.\n• Explain how your design applies OOP principles.'),
(7, 7, 'Computational Biology DNA Analyzer', 'A research assistant needs a program to perform basic analysis on DNA sequences.', '• Accept and validate a DNA sequence containing A, T, G, and C.\n• Calculate sequence length and nucleotide frequencies.\n• Calculate GC percentage.\n• Handle empty or invalid sequences.\n• Prepare your code and interpretation of the result.');

-- --------------------------------------------------------

--
-- Table structure for table `case_study_reviews`
--

CREATE TABLE `case_study_reviews` (
  `id` int(11) NOT NULL,
  `mentoring_id` int(11) NOT NULL,
  `case_study_id` int(11) NOT NULL,
  `status` enum('under_review','approved','not_approved') DEFAULT 'under_review',
  `reviewed_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `courses`
--

CREATE TABLE `courses` (
  `id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `category` enum('basic','advanced') NOT NULL,
  `description` text DEFAULT NULL,
  `total_sessions` int(11) NOT NULL,
  `image_url` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `courses`
--

INSERT INTO `courses` (`id`, `name`, `category`, `description`, `total_sessions`, `image_url`) VALUES
(1, 'Python', 'basic', 'Learn Python fundamentals from syntax to functions and collections.', 5, NULL),
(2, 'C', 'basic', 'Learn C fundamentals including variables, control flow, functions, and memory basics.', 5, NULL),
(3, 'Java', 'basic', 'Learn Java fundamentals and object-oriented programming basics.', 5, NULL),
(4, 'JavaScript', 'basic', 'Learn JavaScript fundamentals for programming and web interactivity.', 5, NULL),
(5, 'Data Structures', 'advanced', 'Learn arrays, linked lists, stacks, queues, trees, and graphs.', 5, NULL),
(6, 'Object-Oriented Programming', 'advanced', 'Learn classes, objects, inheritance, encapsulation, and polymorphism.', 5, NULL),
(7, 'Computational Biology', 'advanced', 'Learn basic computational approaches used in biological data analysis.', 5, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `materials`
--

CREATE TABLE `materials` (
  `id` int(11) NOT NULL,
  `session_id` int(11) NOT NULL,
  `title` varchar(150) NOT NULL,
  `type` enum('pdf','youtube','website') NOT NULL,
  `url` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `materials`
--

INSERT INTO `materials` (`id`, `session_id`, `title`, `type`, `url`) VALUES
(1, 1, 'Python Session 1 Slides', 'pdf', 'http://10.0.2.2:3001/materials/python/session1.pdf'),
(2, 1, 'Learn Python - Full Course for Beginners', 'youtube', 'https://www.youtube.com/watch?v=rfscVS0vtbw'),
(3, 1, 'Official Python Tutorial', 'website', 'https://docs.python.org/3/tutorial/'),
(4, 2, 'Python Session 2 Slides', 'pdf', 'http://10.0.2.2:3001/materials/python/session2.pdf'),
(5, 2, 'Python Variables and Data Types', 'youtube', 'https://www.youtube.com/watch?v=KeA39II7AO8'),
(6, 2, 'Python Data Types and Basic Concepts', 'website', 'https://docs.python.org/3/tutorial/introduction.html'),
(7, 3, 'Python Session 3 Slides', 'pdf', 'http://10.0.2.2:3001/materials/python/session3.pdf'),
(8, 3, 'Python Conditionals and Loops', 'youtube', 'https://www.youtube.com/watch?v=ix9cRaBkVe0'),
(9, 3, 'Python Control Flow', 'website', 'https://docs.python.org/3/tutorial/controlflow.html'),
(10, 4, 'Python Session 4 Slides', 'pdf', 'http://10.0.2.2:3001/materials/python/session4.pdf'),
(11, 4, 'Python Functions and Collections', 'youtube', 'https://www.youtube.com/watch?v=oDOw5tB3Udw'),
(12, 4, 'Python Data Structures and Functions', 'website', 'https://docs.python.org/3/tutorial/datastructures.html'),
(13, 5, 'Python Session 5 Slides', 'pdf', 'http://10.0.2.2:3001/materials/python/session5.pdf'),
(14, 5, 'Python Beginner Projects', 'youtube', 'https://www.youtube.com/watch?v=oDOw5tB3Udw'),
(15, 5, 'Python Project Ideas', 'website', 'https://realpython.com/tutorials/projects/'),
(16, 6, 'C Session 1 Slides', 'pdf', 'http://10.0.2.2:3001/materials/c/session1.pdf'),
(17, 6, 'C Programming Introduction', 'youtube', 'https://www.youtube.com/watch?v=KJgsSFOSQv0'),
(18, 6, 'Learn C Introduction', 'website', 'https://www.learn-c.org/en/'),
(19, 7, 'C Session 2 Slides', 'pdf', 'http://10.0.2.2:3001/materials/c/session2.pdf'),
(20, 7, 'C Variables and Data Types', 'youtube', 'https://www.youtube.com/watch?v=KJgsSFOSQv0'),
(21, 7, 'C Variables and Types', 'website', 'https://www.learn-c.org/en/Variables_and_Types'),
(22, 8, 'C Session 3 Slides', 'pdf', 'http://10.0.2.2:3001/materials/c/session3.pdf'),
(23, 8, 'C Conditionals and Loops', 'youtube', 'https://www.youtube.com/watch?v=KJgsSFOSQv0'),
(24, 8, 'C Conditions', 'website', 'https://www.learn-c.org/en/Conditions'),
(25, 9, 'C Session 4 Slides', 'pdf', 'http://10.0.2.2:3001/materials/c/session4.pdf'),
(26, 9, 'C Functions and Arrays', 'youtube', 'https://www.youtube.com/watch?v=KJgsSFOSQv0'),
(27, 9, 'C Functions', 'website', 'https://www.learn-c.org/en/Functions'),
(28, 10, 'C Session 5 Slides', 'pdf', 'http://10.0.2.2:3001/materials/c/session5.pdf'),
(29, 10, 'C Final Session Review', 'youtube', 'https://www.youtube.com/watch?v=KJgsSFOSQv0'),
(30, 10, 'C Arrays', 'website', 'https://www.learn-c.org/en/Arrays'),
(31, 11, 'Java Session 1 Slides', 'pdf', 'http://10.0.2.2:3001/materials/java/session1.pdf'),
(32, 11, 'Java Programming for Beginners', 'youtube', 'https://www.youtube.com/watch?v=A74TOX803D0'),
(33, 11, 'Java Language Basics', 'website', 'https://dev.java/learn/language/constructs/'),
(34, 12, 'Java Session 2 Slides', 'pdf', 'http://10.0.2.2:3001/materials/java/session2.pdf'),
(35, 12, 'Java Variables and Data Types', 'youtube', 'https://www.youtube.com/watch?v=A74TOX803D0'),
(36, 12, 'Java Variables and Primitive Types', 'website', 'https://dev.java/learn/language/constructs/basics/'),
(37, 13, 'Java Session 3 Slides', 'pdf', 'http://10.0.2.2:3001/materials/java/session3.pdf'),
(38, 13, 'Java Conditionals and Loops', 'youtube', 'https://www.youtube.com/watch?v=A74TOX803D0'),
(39, 13, 'Java Control Flow Statements', 'website', 'https://dev.java/learn/language/constructs/basics/controlling-flow/'),
(40, 14, 'Java Session 4 Slides', 'pdf', 'http://10.0.2.2:3001/materials/java/session4.pdf'),
(41, 14, 'Java Methods and Classes', 'youtube', 'https://www.youtube.com/watch?v=A74TOX803D0'),
(42, 14, 'Java Classes and Objects', 'website', 'https://dev.java/learn/classes-objects/'),
(43, 15, 'Java Session 5 Slides', 'pdf', 'http://10.0.2.2:3001/materials/java/session5.pdf'),
(44, 15, 'Java Final Session Review', 'youtube', 'https://www.youtube.com/watch?v=A74TOX803D0'),
(45, 15, 'Creating Java Classes', 'website', 'https://dev.java/learn/language/oop/classes-objects/creating-classes/'),
(46, 16, 'JavaScript Session 1 Slides', 'pdf', 'http://10.0.2.2:3001/materials/javascript/session1.pdf'),
(47, 16, 'JavaScript Course for Beginners', 'youtube', 'https://www.youtube.com/watch?v=Zi-Q0t4gMC8'),
(48, 16, 'JavaScript Introduction', 'website', 'https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Introduction'),
(49, 17, 'JavaScript Session 2 Slides', 'pdf', 'http://10.0.2.2:3001/materials/javascript/session2.pdf'),
(50, 17, 'JavaScript Variables and Data Types', 'youtube', 'https://www.youtube.com/watch?v=Zi-Q0t4gMC8'),
(51, 17, 'JavaScript Grammar and Types', 'website', 'https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Grammar_and_types'),
(52, 18, 'JavaScript Session 3 Slides', 'pdf', 'http://10.0.2.2:3001/materials/javascript/session3.pdf'),
(53, 18, 'JavaScript Conditionals and Loops', 'youtube', 'https://www.youtube.com/watch?v=Zi-Q0t4gMC8'),
(54, 18, 'JavaScript Control Flow', 'website', 'https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Control_flow_and_error_handling'),
(55, 19, 'JavaScript Session 4 Slides', 'pdf', 'http://10.0.2.2:3001/materials/javascript/session4.pdf'),
(56, 19, 'JavaScript Functions and Arrays', 'youtube', 'https://www.youtube.com/watch?v=Zi-Q0t4gMC8'),
(57, 19, 'JavaScript Functions', 'website', 'https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Functions'),
(58, 20, 'JavaScript Session 5 Slides', 'pdf', 'http://10.0.2.2:3001/materials/javascript/session5.pdf'),
(59, 20, 'JavaScript Final Session Review', 'youtube', 'https://www.youtube.com/watch?v=Zi-Q0t4gMC8'),
(60, 20, 'JavaScript Guide', 'website', 'https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide'),
(61, 21, 'Data Structures Session 1 Slides', 'pdf', 'http://10.0.2.2:3001/materials/data-structures/session1.pdf'),
(62, 21, 'Data Structures and Big-O', 'youtube', 'https://www.youtube.com/watch?v=RBSGKlAvoiM'),
(63, 21, 'Data Structure and Types', 'website', 'https://www.programiz.com/dsa/data-structure-types'),
(64, 22, 'Data Structures Session 2 Slides', 'pdf', 'http://10.0.2.2:3001/materials/data-structures/session2.pdf'),
(65, 22, 'Linked Lists', 'youtube', 'https://www.youtube.com/watch?v=RBSGKlAvoiM'),
(66, 22, 'Linked List Data Structure', 'website', 'https://www.programiz.com/dsa/linked-list'),
(67, 23, 'Data Structures Session 3 Slides', 'pdf', 'http://10.0.2.2:3001/materials/data-structures/session3.pdf'),
(68, 23, 'Stacks and Queues', 'youtube', 'https://www.youtube.com/watch?v=RBSGKlAvoiM'),
(69, 23, 'Stack Data Structure', 'website', 'https://www.programiz.com/dsa/stack'),
(70, 24, 'Data Structures Session 4 Slides', 'pdf', 'http://10.0.2.2:3001/materials/data-structures/session4.pdf'),
(71, 24, 'Trees and Graphs', 'youtube', 'https://www.youtube.com/watch?v=B31LgI4Y4DQ'),
(72, 24, 'Tree Data Structure', 'website', 'https://www.programiz.com/dsa/trees'),
(73, 25, 'Data Structures Session 5 Slides', 'pdf', 'http://10.0.2.2:3001/materials/data-structures/session5.pdf'),
(74, 25, 'Data Structures Final Review', 'youtube', 'https://www.youtube.com/watch?v=B31LgI4Y4DQ'),
(75, 25, 'Graph Data Structure', 'website', 'https://www.programiz.com/dsa/graph'),
(76, 26, 'OOP Session 1 Slides', 'pdf', 'http://10.0.2.2:3001/materials/oop/session1.pdf'),
(77, 26, 'Java Object-Oriented Programming', 'youtube', 'https://www.youtube.com/watch?v=A74TOX803D0'),
(78, 26, 'Java Classes and Objects', 'website', 'https://dev.java/learn/language/oop/classes-objects/'),
(79, 27, 'OOP Session 2 Slides', 'pdf', 'http://10.0.2.2:3001/materials/oop/session2.pdf'),
(80, 27, 'Java Encapsulation', 'youtube', 'https://www.youtube.com/watch?v=A74TOX803D0'),
(81, 27, 'Object-Oriented Programming Concepts', 'website', 'https://dev.java/learn/oop/'),
(82, 28, 'OOP Session 3 Slides', 'pdf', 'http://10.0.2.2:3001/materials/oop/session3.pdf'),
(83, 28, 'Java Inheritance', 'youtube', 'https://www.youtube.com/watch?v=grEKMHGYyns'),
(84, 28, 'Java Inheritance', 'website', 'https://dev.java/learn/language/oop/inheritance/'),
(85, 29, 'OOP Session 4 Slides', 'pdf', 'http://10.0.2.2:3001/materials/oop/session4.pdf'),
(86, 29, 'Java Polymorphism and Abstraction', 'youtube', 'https://www.youtube.com/watch?v=grEKMHGYyns'),
(87, 29, 'Java Inheritance and Polymorphism', 'website', 'https://dev.java/learn/inheritance/'),
(88, 30, 'OOP Session 5 Slides', 'pdf', 'http://10.0.2.2:3001/materials/oop/session5.pdf'),
(89, 30, 'OOP Final Session Review', 'youtube', 'https://www.youtube.com/watch?v=A74TOX803D0'),
(90, 30, 'Java Object-Oriented Programming', 'website', 'https://dev.java/learn/language/oop/'),
(91, 31, 'Computational Biology Session 1 Slides', 'pdf', 'http://10.0.2.2:3001/materials/computational-biology/session1.pdf'),
(92, 31, 'Introduction to Bioinformatics', 'youtube', 'https://www.youtube.com/watch?v=KM2aBG_NBeo'),
(93, 31, 'Introductory Bioinformatics', 'website', 'https://www.ebi.ac.uk/training/online/courses/introductory-bioinformatics-pathway/'),
(94, 32, 'Computational Biology Session 2 Slides', 'pdf', 'http://10.0.2.2:3001/materials/computational-biology/session2.pdf'),
(95, 32, 'Biological Data and Bioinformatics', 'youtube', 'https://www.youtube.com/watch?v=KM2aBG_NBeo'),
(96, 32, 'A Journey Through Bioinformatics', 'website', 'https://www.ebi.ac.uk/training/online/courses/a-journey-through-bioinformatics/'),
(97, 33, 'Computational Biology Session 3 Slides', 'pdf', 'http://10.0.2.2:3001/materials/computational-biology/session3.pdf'),
(98, 33, 'Sequence Analysis Basics', 'youtube', 'https://www.youtube.com/watch?v=KM2aBG_NBeo'),
(99, 33, 'Guide to Sequence Analysis Tools', 'website', 'https://www.ebi.ac.uk/training/online/courses/guide-to-sequence-analysis-tools/'),
(100, 34, 'Computational Biology Session 4 Slides', 'pdf', 'http://10.0.2.2:3001/materials/computational-biology/session4.pdf'),
(101, 34, 'Bioinformatics Tools and Analysis', 'youtube', 'https://www.youtube.com/watch?v=KM2aBG_NBeo'),
(102, 34, 'Bioinformatics Tools and Data Retrieval', 'website', 'https://www.ebi.ac.uk/training/events/bioinformatics-tools-and-data-retrieval/'),
(103, 35, 'Computational Biology Session 5 Slides', 'pdf', 'http://10.0.2.2:3001/materials/computational-biology/session5.pdf'),
(104, 35, 'Computational Biology Final Review', 'youtube', 'https://www.youtube.com/watch?v=KM2aBG_NBeo'),
(105, 35, 'Exploring Biological Sequences', 'website', 'https://www.ebi.ac.uk/training/events/exploring-biological-sequences/');

-- --------------------------------------------------------

--
-- Table structure for table `mentorings`
--

CREATE TABLE `mentorings` (
  `id` int(11) NOT NULL,
  `request_id` int(11) NOT NULL,
  `student_id` int(11) NOT NULL,
  `mentor_id` int(11) NOT NULL,
  `course_id` int(11) NOT NULL,
  `from_session` int(11) NOT NULL,
  `to_session` int(11) DEFAULT NULL,
  `include_final_project` tinyint(1) DEFAULT 0,
  `start_at` datetime NOT NULL,
  `deadline_at` datetime NOT NULL,
  `ended_at` datetime DEFAULT NULL,
  `status` enum('active','completed','expired') DEFAULT 'active'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `mentorings`
--

INSERT INTO `mentorings` (`id`, `request_id`, `student_id`, `mentor_id`, `course_id`, `from_session`, `to_session`, `include_final_project`, `start_at`, `deadline_at`, `ended_at`, `status`) VALUES
(1, 2, 1, 2, 2, 1, 3, 0, '2026-11-15 19:57:29', '2026-12-06 19:57:29', NULL, 'active');

-- --------------------------------------------------------

--
-- Table structure for table `mentoring_requests`
--

CREATE TABLE `mentoring_requests` (
  `id` int(11) NOT NULL,
  `student_id` int(11) NOT NULL,
  `mentor_id` int(11) NOT NULL,
  `course_id` int(11) NOT NULL,
  `from_session` int(11) NOT NULL,
  `to_session` int(11) DEFAULT NULL,
  `include_final_project` tinyint(1) DEFAULT 0,
  `status` enum('pending','accepted','declined') DEFAULT 'pending',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `mentoring_requests`
--

INSERT INTO `mentoring_requests` (`id`, `student_id`, `mentor_id`, `course_id`, `from_session`, `to_session`, `include_final_project`, `status`, `created_at`) VALUES
(1, 1, 2, 2, 1, 3, 0, 'declined', '2026-11-15 12:52:41'),
(2, 1, 2, 2, 1, 3, 0, 'accepted', '2026-11-15 12:56:55');

-- --------------------------------------------------------

--
-- Table structure for table `mentor_certificates`
--

CREATE TABLE `mentor_certificates` (
  `id` int(11) NOT NULL,
  `mentor_id` int(11) NOT NULL,
  `certificate_name` varchar(150) NOT NULL,
  `pdf_url` text NOT NULL,
  `uploaded_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `mentor_specializations`
--

CREATE TABLE `mentor_specializations` (
  `id` int(11) NOT NULL,
  `mentor_id` int(11) NOT NULL,
  `course_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `mentor_specializations`
--

INSERT INTO `mentor_specializations` (`id`, `mentor_id`, `course_id`) VALUES
(4, 2, 1),
(5, 2, 2),
(6, 2, 3),
(7, 2, 5);

-- --------------------------------------------------------

--
-- Table structure for table `messages`
--

CREATE TABLE `messages` (
  `id` int(11) NOT NULL,
  `mentoring_id` int(11) NOT NULL,
  `sender_id` int(11) NOT NULL,
  `message_type` enum('text','image','video','link') DEFAULT 'text',
  `message_text` text DEFAULT NULL,
  `media_url` text DEFAULT NULL,
  `sent_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `notifications`
--

CREATE TABLE `notifications` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `type` varchar(50) NOT NULL,
  `title` varchar(150) NOT NULL,
  `message` text NOT NULL,
  `is_read` tinyint(1) DEFAULT 0,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `quiz_answers`
--

CREATE TABLE `quiz_answers` (
  `id` int(11) NOT NULL,
  `student_id` int(11) NOT NULL,
  `session_id` int(11) NOT NULL,
  `question_id` int(11) NOT NULL,
  `option_id` int(11) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `quiz_options`
--

CREATE TABLE `quiz_options` (
  `id` int(11) NOT NULL,
  `question_id` int(11) NOT NULL,
  `option_text` text NOT NULL,
  `is_correct` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `quiz_options`
--

INSERT INTO `quiz_options` (`id`, `question_id`, `option_text`, `is_correct`) VALUES
(1, 1, 'A high-level programming language', 1),
(2, 1, 'A database management system', 0),
(3, 1, 'An operating system', 0),
(4, 1, 'A web browser', 0),
(5, 2, 'print()', 1),
(6, 2, 'show()', 0),
(7, 2, 'display()', 0),
(8, 2, 'output()', 0),
(9, 3, '#', 1),
(10, 3, '//', 0),
(11, 3, '<!-- -->', 0),
(12, 3, '**', 0),
(13, 4, '.py', 1),
(14, 4, '.java', 0),
(15, 4, '.html', 0),
(16, 4, '.exe', 0),
(17, 5, 'student_name', 1),
(18, 5, '2student', 0),
(19, 5, 'student-name', 0),
(20, 5, 'class', 0),
(21, 6, 'def', 1),
(22, 6, 'function', 0),
(23, 6, 'func', 0),
(24, 6, 'method', 0),
(25, 7, 'High-level interpreted language', 1),
(26, 7, 'Machine language only', 0),
(27, 7, 'Markup language', 0),
(28, 7, 'Database query language', 0),
(29, 8, 'input()', 1),
(30, 8, 'read()', 0),
(31, 8, 'scan()', 0),
(32, 8, 'get()', 0),
(33, 9, '\"Hello\"', 1),
(34, 9, '123', 0),
(35, 9, 'True', 0),
(36, 9, '3.14', 0),
(37, 10, 'Indentation defines blocks of code', 1),
(38, 10, 'Indentation has no meaning', 0),
(39, 10, 'Indentation is only used for comments', 0),
(40, 10, 'Indentation is optional everywhere', 0),
(41, 11, '=', 1),
(42, 11, '==', 0),
(43, 11, '===', 0),
(44, 11, ':=', 0),
(45, 12, 'int', 1),
(46, 12, 'float', 0),
(47, 12, 'str', 0),
(48, 12, 'bool', 0),
(49, 13, 'float', 1),
(50, 13, 'int', 0),
(51, 13, 'str', 0),
(52, 13, 'list', 0),
(53, 14, 'str', 1),
(54, 14, 'int', 0),
(55, 14, 'bool', 0),
(56, 14, 'float', 0),
(57, 15, 'True', 1),
(58, 15, '\"True\"', 0),
(59, 15, '1.5', 0),
(60, 15, '\"Boolean\"', 0),
(61, 16, 'type()', 1),
(62, 16, 'typeof()', 0),
(63, 16, 'datatype()', 0),
(64, 16, 'check()', 0),
(65, 17, 'The integer 10', 1),
(66, 17, 'The string \"10\"', 0),
(67, 17, 'The float 10.0', 0),
(68, 17, 'An error', 0),
(69, 18, 'student_score', 1),
(70, 18, 'student-score', 0),
(71, 18, '2student', 0),
(72, 18, 'student score', 0),
(73, 19, '8', 1),
(74, 19, '53', 0),
(75, 19, '2', 0),
(76, 19, '15', 0),
(77, 20, 'str()', 1),
(78, 20, 'string()', 0),
(79, 20, 'text()', 0),
(80, 20, 'char()', 0),
(81, 21, 'if', 1),
(82, 21, 'when', 0),
(83, 21, 'case', 0),
(84, 21, 'check', 0),
(85, 22, 'elif', 1),
(86, 22, 'elseif', 0),
(87, 22, 'otherwise', 0),
(88, 22, 'then', 0),
(89, 23, 'else', 1),
(90, 23, 'default', 0),
(91, 23, 'otherwise', 0),
(92, 23, 'finally', 0),
(93, 24, 'for', 1),
(94, 24, 'if', 0),
(95, 24, 'switch', 0),
(96, 24, 'case', 0),
(97, 25, 'Its condition', 1),
(98, 25, 'Its variable name', 0),
(99, 25, 'Its print statement', 0),
(100, 25, 'Its function name', 0),
(101, 26, 'break', 1),
(102, 26, 'continue', 0),
(103, 26, 'pass', 0),
(104, 26, 'exitloop', 0),
(105, 27, 'continue', 1),
(106, 27, 'break', 0),
(107, 27, 'stop', 0),
(108, 27, 'return', 0),
(109, 28, '0, 1, 2', 1),
(110, 28, '1, 2, 3', 0),
(111, 28, '0, 1, 2, 3', 0),
(112, 28, '1, 2', 0),
(113, 29, '==', 1),
(114, 29, '=', 0),
(115, 29, '!=', 0),
(116, 29, '>=', 0),
(117, 30, 'Whether x is greater than 5', 1),
(118, 30, 'Whether x equals 5', 0),
(119, 30, 'Whether x is less than 5', 0),
(120, 30, 'Whether x is not a number', 0),
(121, 31, 'def', 1),
(122, 31, 'function', 0),
(123, 31, 'func', 0),
(124, 31, 'method', 0),
(125, 32, 'return', 1),
(126, 32, 'send', 0),
(127, 32, 'output', 0),
(128, 32, 'yieldvalue', 0),
(129, 33, 'list', 1),
(130, 33, 'tuple', 0),
(131, 33, 'set', 0),
(132, 33, 'dictionary', 0),
(133, 34, 'tuple', 1),
(134, 34, 'list', 0),
(135, 34, 'dictionary', 0),
(136, 34, 'string', 0),
(137, 35, 'dictionary', 1),
(138, 35, 'list', 0),
(139, 35, 'tuple', 0),
(140, 35, 'string', 0),
(141, 36, 'append()', 1),
(142, 36, 'add()', 0),
(143, 36, 'push()', 0),
(144, 36, 'insertEnd()', 0),
(145, 37, 'len()', 1),
(146, 37, 'size()', 0),
(147, 37, 'countall()', 0),
(148, 37, 'length()', 0),
(149, 38, '0', 1),
(150, 38, '1', 0),
(151, 38, '-1', 0),
(152, 38, '2', 0),
(153, 39, 'set', 1),
(154, 39, 'list', 0),
(155, 39, 'tuple', 0),
(156, 39, 'string', 0),
(157, 40, 'A value name accepted by a function definition', 1),
(158, 40, 'A loop inside a function', 0),
(159, 40, 'A Python file extension', 0),
(160, 40, 'A type of comment', 0),
(161, 41, 'Functions', 1),
(162, 41, 'Comments', 0),
(163, 41, 'Strings', 0),
(164, 41, 'Print statements only', 0),
(165, 42, 'if', 1),
(166, 42, 'import', 0),
(167, 42, 'def only', 0),
(168, 42, 'print only', 0),
(169, 43, 'list', 1),
(170, 43, 'if', 0),
(171, 43, 'def', 0),
(172, 43, 'print', 0),
(173, 44, '==', 1),
(174, 44, '=', 0),
(175, 44, '+', 0),
(176, 44, '//', 0),
(177, 45, 'for', 1),
(178, 45, 'def', 0),
(179, 45, 'return', 0),
(180, 45, 'else', 0),
(181, 46, 'input()', 1),
(182, 46, 'print()', 0),
(183, 46, 'read()', 0),
(184, 46, 'keyboard()', 0),
(185, 47, 'return', 1),
(186, 47, 'break', 0),
(187, 47, 'continue', 0),
(188, 47, 'print', 0),
(189, 48, 'To define blocks of code', 1),
(190, 48, 'To rename variables', 0),
(191, 48, 'To create files', 0),
(192, 48, 'To install Python', 0),
(193, 49, 'bool', 1),
(194, 49, 'str', 0),
(195, 49, 'float', 0),
(196, 49, 'list', 0),
(197, 50, 'Identify the problem and required features', 1),
(198, 50, 'Write random code immediately', 0),
(199, 50, 'Avoid testing the program', 0),
(200, 50, 'Use only one variable', 0),
(201, 51, 'A general-purpose programming language', 1),
(202, 51, 'A database system', 0),
(203, 51, 'A web browser', 0),
(204, 51, 'A markup language', 0),
(205, 52, 'printf()', 1),
(206, 52, 'print()', 0),
(207, 52, 'display()', 0),
(208, 52, 'echo()', 0),
(209, 53, 'stdio.h', 1),
(210, 53, 'math.h', 0),
(211, 53, 'string.h', 0),
(212, 53, 'stdlib.java', 0),
(213, 54, 'main()', 1),
(214, 54, 'start()', 0),
(215, 54, 'init()', 0),
(216, 54, 'run()', 0),
(217, 55, ';', 1),
(218, 55, ':', 0),
(219, 55, '.', 0),
(220, 55, ',', 0),
(221, 56, '// comment', 1),
(222, 56, '# comment', 0),
(223, 56, '<!-- comment -->', 0),
(224, 56, '-- comment', 0),
(225, 57, '.c', 1),
(226, 57, '.java', 0),
(227, 57, '.py', 0),
(228, 57, '.html', 0),
(229, 58, 'Successful program termination', 1),
(230, 58, 'A syntax error', 0),
(231, 58, 'The program must restart', 0),
(232, 58, 'No output is allowed', 0),
(233, 59, 'Compiler', 1),
(234, 59, 'Web browser', 0),
(235, 59, 'Database', 0),
(236, 59, 'Spreadsheet', 0),
(237, 60, '{ }', 1),
(238, 60, '[ ]', 0),
(239, 60, '< >', 0),
(240, 60, '( ) only', 0),
(241, 61, 'int', 1),
(242, 61, 'float', 0),
(243, 61, 'char[]', 0),
(244, 61, 'void', 0),
(245, 62, 'float', 1),
(246, 62, 'int', 0),
(247, 62, 'char', 0),
(248, 62, 'void', 0),
(249, 63, 'char', 1),
(250, 63, 'string', 0),
(251, 63, 'float', 0),
(252, 63, 'double[]', 0),
(253, 64, 'int age = 20;', 1),
(254, 64, 'age int = 20;', 0),
(255, 64, 'integer age := 20;', 0),
(256, 64, 'var age = 20;', 0),
(257, 65, '=', 1),
(258, 65, '==', 0),
(259, 65, '!=', 0),
(260, 65, '>=', 0),
(261, 66, '%d', 1),
(262, 66, '%f', 0),
(263, 66, '%s', 0),
(264, 66, '%c only for integers', 0),
(265, 67, '%c', 1),
(266, 67, '%d', 0),
(267, 67, '%s', 0),
(268, 67, '%x', 0),
(269, 68, 'scanf()', 1),
(270, 68, 'input()', 0),
(271, 68, 'readline()', 0),
(272, 68, 'cin()', 0),
(273, 69, '8', 1),
(274, 69, '53', 0),
(275, 69, '2', 0),
(276, 69, '15', 0),
(277, 70, 'const', 1),
(278, 70, 'fixed', 0),
(279, 70, 'final', 0),
(280, 70, 'staticvalue', 0),
(281, 71, 'if', 1),
(282, 71, 'when', 0),
(283, 71, 'check', 0),
(284, 71, 'condition', 0),
(285, 72, '==', 1),
(286, 72, '=', 0),
(287, 72, '===', 0),
(288, 72, ':=', 0),
(289, 73, 'else', 1),
(290, 73, 'otherwise', 0),
(291, 73, 'default', 0),
(292, 73, 'then', 0),
(293, 74, 'for', 1),
(294, 74, 'if', 0),
(295, 74, 'switch', 0),
(296, 74, 'return', 0),
(297, 75, 'while', 1),
(298, 75, 'switch', 0),
(299, 75, 'case', 0),
(300, 75, 'goto only', 0),
(301, 76, 'do while', 1),
(302, 76, 'for', 0),
(303, 76, 'if', 0),
(304, 76, 'switch', 0),
(305, 77, 'break', 1),
(306, 77, 'continue', 0),
(307, 77, 'next', 0),
(308, 77, 'skip', 0),
(309, 78, 'continue', 1),
(310, 78, 'break', 0),
(311, 78, 'exit', 0),
(312, 78, 'stop', 0),
(313, 79, '&&', 1),
(314, 79, '||', 0),
(315, 79, '!', 0),
(316, 79, '&=', 0),
(317, 80, 'switch', 1),
(318, 80, 'repeat', 0),
(319, 80, 'function', 0),
(320, 80, 'include', 0),
(321, 81, 'To create reusable blocks of code', 1),
(322, 81, 'To rename the compiler', 0),
(323, 81, 'To create comments', 0),
(324, 81, 'To replace variables', 0),
(325, 82, 'void', 1),
(326, 82, 'empty', 0),
(327, 82, 'null', 0),
(328, 82, 'none', 0),
(329, 83, 'return', 1),
(330, 83, 'send', 0),
(331, 83, 'output', 0),
(332, 83, 'break', 0),
(333, 84, 'int numbers[5];', 1),
(334, 84, 'int numbers(5);', 0),
(335, 84, 'array int numbers[5];', 0),
(336, 84, 'numbers = int[5];', 0),
(337, 85, '0', 1),
(338, 85, '1', 0),
(339, 85, '-1', 0),
(340, 85, 'Depends on the value', 0),
(341, 86, '4', 1),
(342, 86, '5', 0),
(343, 86, '6', 0),
(344, 86, '1', 0),
(345, 87, 'A variable listed in a function definition', 1),
(346, 87, 'A compiler error', 0),
(347, 87, 'An array index only', 0),
(348, 87, 'A comment', 0),
(349, 88, 'values[2]', 1),
(350, 88, 'values[3]', 0),
(351, 88, 'values(2)', 0),
(352, 88, 'values.third', 0),
(353, 89, 'Elements of the declared element type', 1),
(354, 89, 'Every element must have a different type', 0),
(355, 89, 'Only characters', 0),
(356, 89, 'Only floating point numbers', 0),
(357, 90, 'Executing the code defined by that function', 1),
(358, 90, 'Deleting the function', 0),
(359, 90, 'Changing the compiler', 0),
(360, 90, 'Creating an array', 0),
(361, 91, 'An array', 1),
(362, 91, 'An if statement only', 0),
(363, 91, 'A comment', 0),
(364, 91, 'A header filename', 0),
(365, 92, 'Functions', 1),
(366, 92, 'Comments only', 0),
(367, 92, 'Format specifiers only', 0),
(368, 92, 'Semicolons', 0),
(369, 93, 'struct', 1),
(370, 93, 'class', 0),
(371, 93, 'object', 0),
(372, 93, 'record only', 0),
(373, 94, 'if statement', 1),
(374, 94, 'include statement', 0),
(375, 94, 'typedef only', 0),
(376, 94, 'printf only', 0),
(377, 95, 'for loop', 1),
(378, 95, 'include', 0),
(379, 95, 'return', 0),
(380, 95, 'struct only', 0),
(381, 96, 'printf()', 1),
(382, 96, 'scanf()', 0),
(383, 96, 'main()', 0),
(384, 96, 'include()', 0),
(385, 97, 'To prevent invalid data from causing incorrect behavior', 1),
(386, 97, 'To make every variable global', 0),
(387, 97, 'To remove all functions', 0),
(388, 97, 'To avoid using arrays', 0),
(389, 98, '++', 1),
(390, 98, '--', 0),
(391, 98, '==', 0),
(392, 98, '&&', 0),
(393, 99, 'The program should handle the condition and inform the user', 1),
(394, 99, 'The program must always crash', 0),
(395, 99, 'The compiler should be deleted', 0),
(396, 99, 'The array should always be ignored', 0),
(397, 100, 'Test normal and edge cases', 1),
(398, 100, 'Never run the program', 0),
(399, 100, 'Remove all validation', 0),
(400, 100, 'Use no functions', 0),
(401, 101, 'A general-purpose object-oriented programming language', 1),
(402, 101, 'A database management system', 0),
(403, 101, 'A web browser', 0),
(404, 101, 'A markup language', 0),
(405, 102, 'main()', 1),
(406, 102, 'start()', 0),
(407, 102, 'runApp()', 0),
(408, 102, 'init()', 0),
(409, 103, 'System.out.println()', 1),
(410, 103, 'print()', 0),
(411, 103, 'Console.write()', 0),
(412, 103, 'System.printline()', 0),
(413, 104, '.java', 1),
(414, 104, '.class', 0),
(415, 104, '.jar', 0),
(416, 104, '.js', 0),
(417, 105, '.class', 1),
(418, 105, '.java', 0),
(419, 105, '.html', 0),
(420, 105, '.py', 0),
(421, 106, 'Java Virtual Machine', 1),
(422, 106, 'Java Variable Manager', 0),
(423, 106, 'Java Visual Method', 0),
(424, 106, 'Java Version Module', 0),
(425, 107, 'class', 1),
(426, 107, 'object', 0),
(427, 107, 'struct', 0),
(428, 107, 'define', 0),
(429, 108, ';', 1),
(430, 108, ':', 0),
(431, 108, '.', 0),
(432, 108, ',', 0),
(433, 109, '// comment', 1),
(434, 109, '# comment', 0),
(435, 109, '<!-- comment -->', 0),
(436, 109, '-- comment', 0),
(437, 110, 'javac', 1),
(438, 110, 'javafile', 0),
(439, 110, 'compilejava', 0),
(440, 110, 'javarun', 0),
(441, 111, 'int', 1),
(442, 111, 'String', 0),
(443, 111, 'boolean', 0),
(444, 111, 'char', 0),
(445, 112, 'boolean', 1),
(446, 112, 'int', 0),
(447, 112, 'String', 0),
(448, 112, 'double', 0),
(449, 113, 'char', 1),
(450, 113, 'String', 0),
(451, 113, 'text', 0),
(452, 113, 'character', 0),
(453, 114, 'double', 1),
(454, 114, 'int', 0),
(455, 114, 'boolean', 0),
(456, 114, 'char', 0),
(457, 115, 'int age = 20;', 1),
(458, 115, 'age int = 20;', 0),
(459, 115, 'var age := 20;', 0),
(460, 115, 'integer age : 20;', 0),
(461, 116, '=', 1),
(462, 116, '==', 0),
(463, 116, '!=', 0),
(464, 116, '===', 0),
(465, 117, 'String', 1),
(466, 117, 'Text', 0),
(467, 117, 'CharArray', 0),
(468, 117, 'Word', 0),
(469, 118, 'final', 1),
(470, 118, 'const', 0),
(471, 118, 'fixed', 0),
(472, 118, 'immutable', 0),
(473, 119, '8', 1),
(474, 119, '53', 0),
(475, 119, '15', 0),
(476, 119, '2', 0),
(477, 120, 'long', 1),
(478, 120, 'String', 0),
(479, 120, 'Array', 0),
(480, 120, 'Object', 0),
(481, 121, 'if', 1),
(482, 121, 'when', 0),
(483, 121, 'condition', 0),
(484, 121, 'check', 0),
(485, 122, 'else', 1),
(486, 122, 'otherwise', 0),
(487, 122, 'default', 0),
(488, 122, 'then', 0),
(489, 123, '==', 1),
(490, 123, '=', 0),
(491, 123, '===', 0),
(492, 123, ':=', 0),
(493, 124, 'for', 1),
(494, 124, 'if', 0),
(495, 124, 'switch', 0),
(496, 124, 'return', 0),
(497, 125, 'while', 1),
(498, 125, 'if', 0),
(499, 125, 'switch', 0),
(500, 125, 'case', 0),
(501, 126, 'do-while', 1),
(502, 126, 'for', 0),
(503, 126, 'if', 0),
(504, 126, 'switch', 0),
(505, 127, 'break', 1),
(506, 127, 'continue', 0),
(507, 127, 'next', 0),
(508, 127, 'skip', 0),
(509, 128, 'continue', 1),
(510, 128, 'break', 0),
(511, 128, 'stop', 0),
(512, 128, 'exit', 0),
(513, 129, '&&', 1),
(514, 129, '||', 0),
(515, 129, '!', 0),
(516, 129, '==', 0),
(517, 130, 'switch', 1),
(518, 130, 'import', 0),
(519, 130, 'class', 0),
(520, 130, 'package', 0),
(521, 131, 'A blueprint used to create objects', 1),
(522, 131, 'A loop statement', 0),
(523, 131, 'A primitive data type', 0),
(524, 131, 'A compiler command', 0),
(525, 132, 'new', 1),
(526, 132, 'create', 0),
(527, 132, 'object', 0),
(528, 132, 'instance', 0),
(529, 133, 'A block of code that performs a behavior or operation', 1),
(530, 133, 'A Java source file extension', 0),
(531, 133, 'A primitive type', 0),
(532, 133, 'A package name only', 0),
(533, 134, 'To initialize a newly created object', 1),
(534, 134, 'To terminate the JVM', 0),
(535, 134, 'To create a loop', 0),
(536, 134, 'To import another package', 0),
(537, 135, 'private', 1),
(538, 135, 'public', 0),
(539, 135, 'global', 0),
(540, 135, 'open', 0),
(541, 136, 'this', 1),
(542, 136, 'self', 0),
(543, 136, 'current', 0),
(544, 136, 'object', 0),
(545, 137, 'void', 1),
(546, 137, 'null', 0),
(547, 137, 'empty', 0),
(548, 137, 'none', 0),
(549, 138, 'A variable declared in a method or constructor declaration to receive a value', 1),
(550, 138, 'A type of loop', 0),
(551, 138, 'A compiler output file', 0),
(552, 138, 'A package folder only', 0),
(553, 139, 'car.start()', 1),
(554, 139, 'car->start()', 0),
(555, 139, 'start.car()', 0),
(556, 139, 'car::start()', 0),
(557, 140, 'static member', 1),
(558, 140, 'local variable', 0),
(559, 140, 'method parameter', 0),
(560, 140, 'constructor parameter', 0),
(561, 141, 'Classes and objects', 1),
(562, 141, 'Comments only', 0),
(563, 141, 'Primitive integers only', 0),
(564, 141, 'Import statements only', 0),
(565, 142, 'Array', 1),
(566, 142, 'if statement', 0),
(567, 142, 'constructor', 0),
(568, 142, 'package', 0),
(569, 143, '0', 1),
(570, 143, '1', 0),
(571, 143, '-1', 0),
(572, 143, 'Depends on the JVM', 0),
(573, 144, 'if statement', 1),
(574, 144, 'import statement', 0),
(575, 144, 'package statement', 0),
(576, 144, 'class declaration only', 0),
(577, 145, 'Encapsulation', 1),
(578, 145, 'Compilation', 0),
(579, 145, 'Iteration', 0),
(580, 145, 'Commenting', 0),
(581, 146, 'A return value and return statement', 1),
(582, 146, 'A package declaration', 0),
(583, 146, 'A comment', 0),
(584, 146, 'An import statement', 0),
(585, 147, 'A loop', 1),
(586, 147, 'A comment', 0),
(587, 147, 'A package declaration', 0),
(588, 147, 'A class name only', 0),
(589, 148, 'To prevent invalid application state and incorrect bookings', 1),
(590, 148, 'To remove all classes', 0),
(591, 148, 'To stop using methods', 0),
(592, 148, 'To prevent Java from compiling any code', 0),
(593, 149, 'new', 1),
(594, 149, 'make', 0),
(595, 149, 'instance', 0),
(596, 149, 'alloc', 0),
(597, 150, 'Test both normal cases and edge cases', 1),
(598, 150, 'Never run the program', 0),
(599, 150, 'Remove all input validation', 0),
(600, 150, 'Delete all classes', 0),
(601, 151, 'Adding programming behavior and interactivity to applications and web pages', 1),
(602, 151, 'Only storing database tables', 0),
(603, 151, 'Only creating image files', 0),
(604, 151, 'Replacing every operating system', 0),
(605, 152, 'console.log()', 1),
(606, 152, 'print.console()', 0),
(607, 152, 'System.out.println()', 0),
(608, 152, 'printf()', 0),
(609, 153, '.js', 1),
(610, 153, '.java', 0),
(611, 153, '.py', 0),
(612, 153, '.c', 0),
(613, 154, '// comment', 1),
(614, 154, '# comment', 0),
(615, 154, '<!-- comment -->', 0),
(616, 154, '-- comment', 0),
(617, 155, 'let', 1),
(618, 155, 'int', 0),
(619, 155, 'define', 0),
(620, 155, 'variable', 0),
(621, 156, 'const', 1),
(622, 156, 'fixed', 0),
(623, 156, 'final', 0),
(624, 156, 'static', 0),
(625, 157, 'Yes', 1),
(626, 157, 'No', 0),
(627, 157, 'Only inside arrays', 0),
(628, 157, 'Only for numbers', 0),
(629, 158, '\"CodeLearn\"', 1),
(630, 158, '123', 0),
(631, 158, 'true', 0),
(632, 158, 'null', 0),
(633, 159, 'var', 1),
(634, 159, 'variable', 0),
(635, 159, 'define', 0),
(636, 159, 'field', 0),
(637, 160, 'Ecma International', 1),
(638, 160, 'MySQL', 0),
(639, 160, 'Android Studio', 0),
(640, 160, 'GitHub only', 0),
(641, 161, 'boolean', 1),
(642, 161, 'number', 0),
(643, 161, 'string', 0),
(644, 161, 'array', 0),
(645, 162, 'number', 1),
(646, 162, 'integer', 0),
(647, 162, 'decimal', 0),
(648, 162, 'numericString', 0),
(649, 163, '\"JavaScript\"', 1),
(650, 163, '42', 0),
(651, 163, 'false', 0),
(652, 163, 'null', 0),
(653, 164, '=', 1),
(654, 164, '==', 0),
(655, 164, '===', 0),
(656, 164, '!=', 0),
(657, 165, 'typeof', 1),
(658, 165, 'type', 0),
(659, 165, 'getType', 0),
(660, 165, 'datatype', 0),
(661, 166, 'null', 1),
(662, 166, 'true', 0),
(663, 166, '1', 0),
(664, 166, '\"null\"', 0),
(665, 167, 'A value has not been assigned', 1),
(666, 167, 'The value is always zero', 0),
(667, 167, 'The value is always false', 0),
(668, 167, 'The variable contains an empty array', 0),
(669, 168, 'let age = 20;', 1),
(670, 168, 'int age = 20;', 0),
(671, 168, 'age int = 20;', 0),
(672, 168, 'variable age := 20;', 0),
(673, 169, '8', 1),
(674, 169, '53', 0),
(675, 169, '2', 0),
(676, 169, '15', 0),
(677, 170, 'A variable can hold values of different types at different times', 1),
(678, 170, 'Every variable must declare a fixed type', 0),
(679, 170, 'Only strings can be stored', 0),
(680, 170, 'The program cannot use numbers', 0),
(681, 171, 'if', 1),
(682, 171, 'when', 0),
(683, 171, 'condition', 0),
(684, 171, 'check', 0),
(685, 172, 'else', 1),
(686, 172, 'otherwise', 0),
(687, 172, 'default', 0),
(688, 172, 'then', 0),
(689, 173, '===', 1),
(690, 173, '=', 0),
(691, 173, '=>', 0),
(692, 173, '!==', 0),
(693, 174, 'for', 1),
(694, 174, 'if', 0),
(695, 174, 'switch', 0),
(696, 174, 'return', 0),
(697, 175, 'while', 1),
(698, 175, 'if', 0),
(699, 175, 'case', 0),
(700, 175, 'function', 0),
(701, 176, 'do...while', 1),
(702, 176, 'for', 0),
(703, 176, 'if', 0),
(704, 176, 'switch', 0),
(705, 177, 'break', 1),
(706, 177, 'continue', 0),
(707, 177, 'skip', 0),
(708, 177, 'next', 0),
(709, 178, 'continue', 1),
(710, 178, 'break', 0),
(711, 178, 'stop', 0),
(712, 178, 'returnLoop', 0),
(713, 179, '&&', 1),
(714, 179, '||', 0),
(715, 179, '!', 0),
(716, 179, '===', 0),
(717, 180, 'switch', 1),
(718, 180, 'import', 0),
(719, 180, 'class', 0),
(720, 180, 'const', 0),
(721, 181, 'Creating reusable blocks of behavior', 1),
(722, 181, 'Changing the browser operating system', 0),
(723, 181, 'Deleting JavaScript syntax', 0),
(724, 181, 'Replacing every variable', 0),
(725, 182, 'function', 1),
(726, 182, 'method', 0),
(727, 182, 'def', 0),
(728, 182, 'func', 0),
(729, 183, 'return', 1),
(730, 183, 'send', 0),
(731, 183, 'output', 0),
(732, 183, 'yieldValue', 0),
(733, 184, '[1, 2, 3]', 1),
(734, 184, '{1, 2, 3}', 0),
(735, 184, '(1, 2, 3)', 0),
(736, 184, '<1, 2, 3>', 0),
(737, 185, '0', 1),
(738, 185, '1', 0),
(739, 185, '-1', 0),
(740, 185, 'Depends on the array length', 0),
(741, 186, 'push()', 1),
(742, 186, 'pop()', 0),
(743, 186, 'shift()', 0),
(744, 186, 'slice()', 0),
(745, 187, 'pop()', 1),
(746, 187, 'push()', 0),
(747, 187, 'unshift()', 0),
(748, 187, 'join()', 0),
(749, 188, 'length', 1),
(750, 188, 'size', 0),
(751, 188, 'count', 0),
(752, 188, 'total', 0),
(753, 189, '(x) => x * 2', 1),
(754, 189, '(x) -> x * 2', 0),
(755, 189, 'def x: x * 2', 0),
(756, 189, 'function => x * 2', 0),
(757, 190, 'A variable declared by a function to receive an argument', 1),
(758, 190, 'An HTML element only', 0),
(759, 190, 'An array length property', 0),
(760, 190, 'A browser setting', 0),
(761, 191, 'Array', 1),
(762, 191, 'if statement', 0),
(763, 191, 'Boolean', 0),
(764, 191, 'Comment', 0),
(765, 192, 'Functions', 1),
(766, 192, 'Comments only', 0),
(767, 192, 'String literals only', 0),
(768, 192, 'Numbers only', 0),
(769, 193, 'push()', 1),
(770, 193, 'pop()', 0),
(771, 193, 'shift()', 0),
(772, 193, 'length()', 0),
(773, 194, 'An if statement that validates the input', 1),
(774, 194, 'A comment', 0),
(775, 194, 'A constant containing no logic', 0),
(776, 194, 'An import statement', 0),
(777, 195, 'for loop', 1),
(778, 195, 'if statement', 0),
(779, 195, 'return statement', 0),
(780, 195, 'const declaration only', 0),
(781, 196, '===', 1),
(782, 196, '=', 0),
(783, 196, '=>', 0),
(784, 196, '+=', 0),
(785, 197, 'includes()', 1),
(786, 197, 'containsValue()', 0),
(787, 197, 'hasValue()', 0),
(788, 197, 'exists()', 0),
(789, 198, 'To maintain consistent participant data', 1),
(790, 198, 'To prevent JavaScript from supporting arrays', 0),
(791, 198, 'To remove all functions', 0),
(792, 198, 'To disable the browser', 0),
(793, 199, 'Create separate functions for actions such as add, search, and remove', 1),
(794, 199, 'Put every operation inside one string', 0),
(795, 199, 'Avoid functions entirely', 0),
(796, 199, 'Use comments instead of executable code', 0),
(797, 200, 'Test normal cases and edge cases', 1),
(798, 200, 'Never execute the program', 0),
(799, 200, 'Remove all input validation', 0),
(800, 200, 'Delete all functions', 0),
(801, 201, 'A way of organizing and storing data for efficient access and modification', 1),
(802, 201, 'A programming language', 0),
(803, 201, 'A computer operating system', 0),
(804, 201, 'A database server only', 0),
(805, 202, 'Array', 1),
(806, 202, 'Graph', 0),
(807, 202, 'Tree', 0),
(808, 202, 'Queue only', 0),
(809, 203, '0', 1),
(810, 203, '1', 0),
(811, 203, '-1', 0),
(812, 203, '10', 0),
(813, 204, 'How an algorithm grows in time or space as input size grows', 1),
(814, 204, 'The exact number of code lines', 0),
(815, 204, 'The name of a variable', 0),
(816, 204, 'The output data type', 0),
(817, 205, 'Constant-time complexity', 1),
(818, 205, 'Linear complexity', 0),
(819, 205, 'Quadratic complexity', 0),
(820, 205, 'Exponential complexity', 0),
(821, 206, 'Linear complexity', 1),
(822, 206, 'Constant complexity', 0),
(823, 206, 'Logarithmic complexity', 0),
(824, 206, 'Factorial complexity', 0),
(825, 207, 'Accessing an element by index', 1),
(826, 207, 'Searching an unsorted array for an unknown value', 0),
(827, 207, 'Sorting an entire array', 0),
(828, 207, 'Traversing every element', 0),
(829, 208, 'Fast indexed access', 1),
(830, 208, 'Every element can have unlimited links', 0),
(831, 208, 'They always grow automatically', 0),
(832, 208, 'They never use memory', 0),
(833, 209, 'O(n^2)', 1),
(834, 209, 'O(1)', 0),
(835, 209, 'O(log n)', 0),
(836, 209, 'O(0)', 0),
(837, 210, 'It helps compare algorithm efficiency as input size changes', 1),
(838, 210, 'It guarantees every program has no bugs', 0),
(839, 210, 'It replaces all testing', 0),
(840, 210, 'It automatically writes the program', 0),
(841, 211, 'Nodes connected by links or references', 1),
(842, 211, 'Only contiguous array cells', 0),
(843, 211, 'Only database rows', 0),
(844, 211, 'Only functions', 0),
(845, 212, 'Data and a reference to the next node', 1),
(846, 212, 'Only an index', 0),
(847, 212, 'Only a function', 0),
(848, 212, 'Only a file path', 0),
(849, 213, 'Head', 1),
(850, 213, 'Root', 0),
(851, 213, 'Rear', 0),
(852, 213, 'Top', 0),
(853, 214, 'A null reference from the final node', 1),
(854, 214, 'Index zero', 0),
(855, 214, 'A queue front', 0),
(856, 214, 'A tree root', 0),
(857, 215, 'Inserting a new head node', 1),
(858, 215, 'Random access by arbitrary index', 0),
(859, 215, 'Binary search without additional structure', 0),
(860, 215, 'Sorting automatically', 0),
(861, 216, 'Linked-list nodes do not need to occupy contiguous memory locations', 1),
(862, 216, 'Linked lists cannot store data', 0),
(863, 216, 'Arrays cannot store numbers', 0),
(864, 216, 'Linked lists have no nodes', 0),
(865, 217, 'A reference to the previous node', 1),
(866, 217, 'A reference to a database only', 0),
(867, 217, 'A reference to a compiler', 0),
(868, 217, 'A reference to an array index only', 0),
(869, 218, 'Nodes generally must be followed sequentially from a known node', 1),
(870, 218, 'Linked lists cannot be traversed', 0),
(871, 218, 'Arrays have no indexes', 0),
(872, 218, 'Linked lists store no references', 0),
(873, 219, 'Deletion', 1),
(874, 219, 'Compilation', 0),
(875, 219, 'Formatting', 0),
(876, 219, 'Encryption', 0),
(877, 220, 'Stack or queue', 1),
(878, 220, 'Only comments', 0),
(879, 220, 'Only variables', 0),
(880, 220, 'Only primitive data types', 0),
(881, 221, 'Last In First Out', 1),
(882, 221, 'First In First Out', 0),
(883, 221, 'Random In Random Out', 0),
(884, 221, 'Highest Value First', 0),
(885, 222, 'push', 1),
(886, 222, 'enqueue', 0),
(887, 222, 'dequeue', 0),
(888, 222, 'shift', 0),
(889, 223, 'pop', 1),
(890, 223, 'push', 0),
(891, 223, 'enqueue', 0),
(892, 223, 'append', 0),
(893, 224, 'First In First Out', 1),
(894, 224, 'Last In First Out', 0),
(895, 224, 'Random Access', 0),
(896, 224, 'Highest Priority Only', 0),
(897, 225, 'enqueue', 1),
(898, 225, 'dequeue', 0),
(899, 225, 'pop', 0),
(900, 225, 'peek only', 0),
(901, 226, 'dequeue', 1),
(902, 226, 'enqueue', 0),
(903, 226, 'push', 0),
(904, 226, 'append', 0),
(905, 227, 'Returns the top element without removing it', 1),
(906, 227, 'Removes every element', 0),
(907, 227, 'Adds a new queue', 0),
(908, 227, 'Sorts the stack', 0),
(909, 228, 'People waiting in line for service', 1),
(910, 228, 'A pile of plates', 0),
(911, 228, 'A tree hierarchy', 0),
(912, 228, 'A graph network', 0),
(913, 229, 'A pile of plates', 1),
(914, 229, 'A line of people at a ticket counter', 0),
(915, 229, 'A road network', 0),
(916, 229, 'A family tree', 0),
(917, 230, 'Whether the stack is empty', 1),
(918, 230, 'Whether the queue is full', 0),
(919, 230, 'Whether the array is sorted', 0),
(920, 230, 'Whether the graph is directed', 0),
(921, 231, 'A non-linear hierarchical data structure', 1),
(922, 231, 'A FIFO structure only', 0),
(923, 231, 'A LIFO structure only', 0),
(924, 231, 'A primitive data type', 0),
(925, 232, 'Root', 1),
(926, 232, 'Front', 0),
(927, 232, 'Top', 0),
(928, 232, 'Rear', 0),
(929, 233, 'A node with no children', 1),
(930, 233, 'The root node only', 0),
(931, 233, 'A node with exactly two parents', 0),
(932, 233, 'A queue element', 0),
(933, 234, 'A tree where each node has at most two children', 1),
(934, 234, 'A tree with exactly two nodes', 0),
(935, 234, 'A graph with no edges', 0),
(936, 234, 'A stack containing two items', 0),
(937, 235, 'Vertices and edges', 1),
(938, 235, 'Only arrays', 0),
(939, 235, 'Only roots', 0),
(940, 235, 'Only stacks', 0),
(941, 236, 'A connection between vertices', 1),
(942, 236, 'The first index of an array', 0),
(943, 236, 'The top of a stack', 0),
(944, 236, 'The rear of a queue', 0),
(945, 237, 'Adjacency matrix', 1),
(946, 237, 'Stack list', 0),
(947, 237, 'Queue matrix', 0),
(948, 237, 'Binary array only', 0),
(949, 238, 'Adjacency list', 1),
(950, 238, 'Stack pointer', 0),
(951, 238, 'Array root', 0),
(952, 238, 'Queue tree', 0),
(953, 239, 'Breadth-First Search', 1),
(954, 239, 'Depth-First Search only', 0),
(955, 239, 'Binary search', 0),
(956, 239, 'Linear sort', 0),
(957, 240, 'Depth-First Search', 1),
(958, 240, 'Breadth-First Search only', 0),
(959, 240, 'Bubble sort', 0),
(960, 240, 'Linear search', 0),
(961, 241, 'Stack', 1),
(962, 241, 'Queue', 0),
(963, 241, 'Graph only', 0),
(964, 241, 'Tree only', 0),
(965, 242, 'Queue', 1),
(966, 242, 'Stack', 0),
(967, 242, 'Binary tree only', 0),
(968, 242, 'Graph only', 0),
(969, 243, 'Graph', 1),
(970, 243, 'Stack', 0),
(971, 243, 'Single variable', 0),
(972, 243, 'Primitive integer only', 0),
(973, 244, 'Tree', 1),
(974, 244, 'Queue', 0),
(975, 244, 'Stack only', 0),
(976, 244, 'String only', 0),
(977, 245, 'Linked list', 1),
(978, 245, 'Fixed array only', 0),
(979, 245, 'Graph matrix only', 0),
(980, 245, 'Primitive variable', 0),
(981, 246, 'The operations, performance needs, and structure of the problem', 1),
(982, 246, 'Only the name of the program', 0),
(983, 246, 'Only the programming language logo', 0),
(984, 246, 'Only the number of comments', 0),
(985, 247, 'Pop', 1),
(986, 247, 'Push', 0),
(987, 247, 'Enqueue', 0),
(988, 247, 'Graph traversal', 0),
(989, 248, 'A customer service waiting line', 1),
(990, 248, 'Browser back history', 0),
(991, 248, 'Recursive call tracking', 0),
(992, 248, 'Expression evaluation stack', 0),
(993, 249, 'It verifies behavior for conditions such as empty or full structures', 1),
(994, 249, 'It removes the need for algorithms', 0),
(995, 249, 'It guarantees zero memory usage', 0),
(996, 249, 'It converts every structure into an array', 0),
(997, 250, 'Test the implementation and explain why the chosen structures fit the problem', 1),
(998, 250, 'Avoid testing edge cases', 0),
(999, 250, 'Remove all data structures', 0),
(1000, 250, 'Delete the program before review', 0),
(1001, 251, 'A blueprint used to create objects', 1),
(1002, 251, 'A loop statement', 0),
(1003, 251, 'A primitive value', 0),
(1004, 251, 'A database table only', 0),
(1005, 252, 'An instance of a class', 1),
(1006, 252, 'A source-code comment', 0),
(1007, 252, 'A loop condition', 0),
(1008, 252, 'A package name only', 0),
(1009, 253, 'Fields or attributes', 1),
(1010, 253, 'Comments only', 0),
(1011, 253, 'Package names', 0),
(1012, 253, 'Import statements', 0),
(1013, 254, 'Methods', 1),
(1014, 254, 'File extensions', 0),
(1015, 254, 'Comments', 0),
(1016, 254, 'Variable names only', 0),
(1017, 255, 'new', 1),
(1018, 255, 'create', 0),
(1019, 255, 'object', 0),
(1020, 255, 'instance', 0),
(1021, 256, 'Initializing a newly created object', 1),
(1022, 256, 'Deleting the class', 0),
(1023, 256, 'Creating loops', 0),
(1024, 256, 'Importing packages', 0),
(1025, 257, '.', 1),
(1026, 257, '->', 0),
(1027, 257, ':: only', 0),
(1028, 257, '#', 0),
(1029, 258, 'A reference to a Car object', 1),
(1030, 258, 'A method', 0),
(1031, 258, 'A package', 0),
(1032, 258, 'A primitive integer', 0),
(1033, 259, 'They group related state and behavior into reusable designs', 1),
(1034, 259, 'They eliminate all need for methods', 0),
(1035, 259, 'They prevent objects from existing', 0),
(1036, 259, 'They replace programming languages', 0),
(1037, 260, 'They can have different state while sharing the same class design', 1),
(1038, 260, 'They must always contain identical field values', 0),
(1039, 260, 'Only one object may exist per class', 0),
(1040, 260, 'Objects cannot call methods', 0),
(1041, 261, 'Bundling data and behavior while controlling access to internal state', 1),
(1042, 261, 'Creating only global variables', 0),
(1043, 261, 'Removing every method', 0),
(1044, 261, 'Running multiple loops', 0),
(1045, 262, 'private', 1),
(1046, 262, 'public', 0),
(1047, 262, 'open', 0),
(1048, 262, 'global', 0),
(1049, 263, 'Reading a controlled value from an object', 1),
(1050, 263, 'Deleting the object', 0),
(1051, 263, 'Creating inheritance', 0),
(1052, 263, 'Compiling Java code', 0),
(1053, 264, 'Updating an object value through controlled logic', 1),
(1054, 264, 'Creating a subclass', 0),
(1055, 264, 'Starting the JVM', 0),
(1056, 264, 'Importing a package', 0),
(1057, 265, 'They can reject invalid values before changing object state', 1),
(1058, 265, 'They automatically create databases', 0),
(1059, 265, 'They remove constructors', 0),
(1060, 265, 'They disable methods', 0),
(1061, 266, 'Private fields accessed through appropriate public methods', 1),
(1062, 266, 'Every field public with no validation', 0),
(1063, 266, 'No fields and no methods', 0),
(1064, 266, 'All variables global', 0),
(1065, 267, 'Keeping implementation details inaccessible when they do not need to be exposed', 1),
(1066, 267, 'Deleting source files', 0),
(1067, 267, 'Encrypting every variable automatically', 0),
(1068, 267, 'Removing all classes', 0),
(1069, 268, 'It can allow invalid object state', 1),
(1070, 268, 'Java cannot store fields', 0),
(1071, 268, 'Objects cannot have methods', 0),
(1072, 268, 'Classes cannot contain data', 0),
(1073, 269, 'Encapsulation', 1),
(1074, 269, 'Iteration', 0),
(1075, 269, 'Compilation', 0),
(1076, 269, 'Recursion only', 0),
(1077, 270, 'Internal implementation can change while keeping a controlled public interface', 1),
(1078, 270, 'Every class must become global', 0),
(1079, 270, 'All fields become immutable automatically', 0),
(1080, 270, 'Inheritance becomes impossible', 0),
(1081, 271, 'Creating a new class based on an existing class', 1),
(1082, 271, 'Creating only local variables', 0),
(1083, 271, 'Removing methods from all classes', 0),
(1084, 271, 'Creating database relationships only', 0),
(1085, 272, 'A class from which another class inherits', 1),
(1086, 272, 'A method with no parameters', 0),
(1087, 272, 'A primitive variable', 0),
(1088, 272, 'A loop statement', 0),
(1089, 273, 'A class derived from another class', 1),
(1090, 273, 'A private field', 0),
(1091, 273, 'A constructor only', 0),
(1092, 273, 'A static variable', 0),
(1093, 274, 'extends', 1),
(1094, 274, 'inherits', 0),
(1095, 274, 'implements', 0),
(1096, 274, 'superclass', 0),
(1097, 275, 'Code reuse of common state and behavior', 1),
(1098, 275, 'It removes all objects', 0),
(1099, 275, 'It prevents methods from being reused', 0),
(1100, 275, 'It makes every field public', 0),
(1101, 276, 'Is-a relationship', 1),
(1102, 276, 'Has-a relationship only', 0),
(1103, 276, 'Contains-file relationship', 0),
(1104, 276, 'Database-only relationship', 0),
(1105, 277, 'A subclass to provide its own implementation of an inherited method', 1),
(1106, 277, 'A subclass to delete the JVM', 0),
(1107, 277, 'A class to avoid all methods', 0),
(1108, 277, 'A constructor to become a field', 0),
(1109, 278, 'super', 1),
(1110, 278, 'parent', 0),
(1111, 278, 'base', 0),
(1112, 278, 'upper', 0),
(1113, 279, 'No', 1),
(1114, 279, 'Yes', 0),
(1115, 279, 'Only if both classes are private', 0),
(1116, 279, 'Only if neither class has methods', 0),
(1117, 280, 'Members such as fields and methods', 1),
(1118, 280, 'The source filename only', 0),
(1119, 280, 'The operating system', 0),
(1120, 280, 'The database schema', 0),
(1121, 281, 'The ability to use a common type while objects provide different behavior', 1),
(1122, 281, 'The removal of every method', 0),
(1123, 281, 'The use of only primitive variables', 0),
(1124, 281, 'The creation of database tables', 0),
(1125, 282, 'Method overriding', 1),
(1126, 282, 'Comments', 0),
(1127, 282, 'Variable declaration only', 0),
(1128, 282, 'Import statements', 0),
(1129, 283, 'Exposing essential behavior while hiding unnecessary implementation details', 1),
(1130, 283, 'Making every field public', 0),
(1131, 283, 'Removing all interfaces', 0),
(1132, 283, 'Creating only loops', 0),
(1133, 284, 'abstract', 1),
(1134, 284, 'virtual', 0),
(1135, 284, 'hidden', 0),
(1136, 284, 'base', 0),
(1137, 285, 'No', 1),
(1138, 285, 'Yes', 0),
(1139, 285, 'Only with two constructors', 0),
(1140, 285, 'Only if all fields are public', 0),
(1141, 286, 'A contract of behavior that implementing classes agree to provide', 1),
(1142, 286, 'A database table', 0),
(1143, 286, 'A private constructor only', 0),
(1144, 286, 'A loop structure', 0),
(1145, 287, 'implements', 1),
(1146, 287, 'extendsInterface', 0),
(1147, 287, 'inherits', 0),
(1148, 287, 'uses', 0),
(1149, 288, 'The subclass implementation can execute at runtime', 1),
(1150, 288, 'The program must always crash', 0),
(1151, 288, 'The superclass reference becomes null', 0),
(1152, 288, 'No method can execute', 0),
(1153, 289, 'Abstraction', 1),
(1154, 289, 'Iteration', 0),
(1155, 289, 'Compilation', 0),
(1156, 289, 'Concatenation', 0),
(1157, 290, 'Abstraction and polymorphism', 1),
(1158, 290, 'Only comments and loops', 0),
(1159, 290, 'Only global variables', 0),
(1160, 290, 'Only static fields', 0),
(1161, 291, 'Encapsulation', 1),
(1162, 291, 'Iteration', 0),
(1163, 291, 'Compilation', 0),
(1164, 291, 'Sorting', 0),
(1165, 292, 'Inheritance', 1),
(1166, 292, 'Encapsulation only', 0),
(1167, 292, 'Iteration', 0),
(1168, 292, 'Compilation', 0),
(1169, 293, 'Polymorphism', 1),
(1170, 293, 'Looping', 0),
(1171, 293, 'Variable declaration', 0),
(1172, 293, 'Commenting', 0),
(1173, 294, 'Abstraction', 1),
(1174, 294, 'Sorting', 0),
(1175, 294, 'Array indexing', 0),
(1176, 294, 'String concatenation', 0),
(1177, 295, 'To protect valid object state', 1),
(1178, 295, 'To make every field public', 0),
(1179, 295, 'To remove constructors', 0),
(1180, 295, 'To avoid using classes', 0),
(1181, 296, 'Has-a relationship', 1),
(1182, 296, 'Is-a relationship', 0),
(1183, 296, 'Loop relationship', 0),
(1184, 296, 'Compilation relationship', 0),
(1185, 297, 'It reduces duplication and makes shared behavior easier to maintain', 1),
(1186, 297, 'It prevents any class from having methods', 0),
(1187, 297, 'It requires every field to be global', 0),
(1188, 297, 'It makes testing impossible', 0),
(1189, 298, 'Private fields with controlled methods', 1),
(1190, 298, 'All fields public with no validation', 0),
(1191, 298, 'No class fields at all', 0),
(1192, 298, 'Only global variables', 0),
(1193, 299, 'Whether the subclass truly represents an is-a relationship with the superclass', 1),
(1194, 299, 'Whether every class has the same name', 0),
(1195, 299, 'Whether all methods are removed', 0),
(1196, 299, 'Whether no object can be created', 0),
(1197, 300, 'Test the design, behavior, validation, and important edge cases', 1),
(1198, 300, 'Remove all encapsulation', 0),
(1199, 300, 'Delete the class hierarchy', 0),
(1200, 300, 'Avoid running the program', 0),
(1201, 301, 'Biology with computational and quantitative methods', 1),
(1202, 301, 'Only web design and graphics', 0),
(1203, 301, 'Only hardware engineering', 0),
(1204, 301, 'Only database administration', 0),
(1205, 302, 'Storing, processing, and analyzing biological data', 1),
(1206, 302, 'Only creating websites', 0),
(1207, 302, 'Only repairing computers', 0),
(1208, 302, 'Only drawing molecular images manually', 0),
(1209, 303, 'Genomics', 1),
(1210, 303, 'Accounting', 0),
(1211, 303, 'Architecture', 0),
(1212, 303, 'Civil engineering only', 0),
(1213, 304, 'DNA', 1),
(1214, 304, 'Glucose', 0),
(1215, 304, 'Water', 0),
(1216, 304, 'ATP only', 0),
(1217, 305, 'RNA', 1),
(1218, 305, 'Lipid', 0),
(1219, 305, 'Glucose', 0),
(1220, 305, 'Calcium', 0),
(1221, 306, 'Amino acids', 1),
(1222, 306, 'Nucleotides only', 0),
(1223, 306, 'Fatty acids only', 0),
(1224, 306, 'Minerals only', 0),
(1225, 307, 'Biological datasets can be too large and complex for manual analysis', 1),
(1226, 307, 'Biology contains no measurable data', 0),
(1227, 307, 'Computers replace all laboratory work', 0),
(1228, 307, 'Computers eliminate the need for scientific interpretation', 0),
(1229, 308, 'Analyzing DNA sequence data', 1),
(1230, 308, 'Changing a monitor cable', 0),
(1231, 308, 'Designing a building floor plan', 0),
(1232, 308, 'Printing an unrelated document', 0),
(1233, 309, 'The complete genetic material of an organism', 1),
(1234, 309, 'A single protein only', 0),
(1235, 309, 'A computer program', 0),
(1236, 309, 'A cell membrane only', 0),
(1237, 310, 'Programming and biological understanding', 1),
(1238, 310, 'Only graphic design', 0),
(1239, 310, 'Only hardware repair', 0),
(1240, 310, 'Only word processing', 0),
(1241, 311, 'A, T, G, C', 1),
(1242, 311, 'A, B, C, D', 0),
(1243, 311, 'R, N, A, P', 0),
(1244, 311, 'X, Y, Z, W', 0),
(1245, 312, 'Uracil', 1),
(1246, 312, 'Guanine', 0),
(1247, 312, 'Cytosine', 0),
(1248, 312, 'Adenine', 0),
(1249, 313, 'The ordered nucleotides in a DNA molecule', 1),
(1250, 313, 'Only the length of a protein', 0),
(1251, 313, 'A list of computer instructions', 0),
(1252, 313, 'Only chromosome images', 0),
(1253, 314, 'The ordered amino acids in a protein', 1),
(1254, 314, 'Only DNA bases', 0),
(1255, 314, 'Only chromosome numbers', 0),
(1256, 314, 'The size of a database', 0),
(1257, 315, 'Biological sequences in a text format', 1),
(1258, 315, 'Only microscope images', 0),
(1259, 315, 'Only audio files', 0),
(1260, 315, 'Compiled programs', 0),
(1261, 316, '>', 1),
(1262, 316, '#', 0),
(1263, 316, '//', 0),
(1264, 316, '@@', 0),
(1265, 317, 'Organizing and retrieving biological information', 1),
(1266, 317, 'Only storing passwords', 0),
(1267, 317, 'Only running operating systems', 0),
(1268, 317, 'Only editing images', 0),
(1269, 318, 'DNA, RNA, or protein sequences', 1),
(1270, 318, 'Only weather data', 0),
(1271, 318, 'Only financial data', 0),
(1272, 318, 'Only image pixels', 0),
(1273, 319, 'Invalid or malformed data can produce misleading analysis', 1),
(1274, 319, 'Validation removes the need for analysis', 0),
(1275, 319, 'Validation makes every sequence identical', 0),
(1276, 319, 'Validation prevents databases from storing data', 0),
(1277, 320, 'A string', 1),
(1278, 320, 'A single Boolean only', 0),
(1279, 320, 'A single integer only', 0),
(1280, 320, 'A null value only', 0),
(1281, 321, 'Comparing biological sequences to identify similarities and differences', 1),
(1282, 321, 'Only counting database rows', 0),
(1283, 321, 'Only compressing files', 0),
(1284, 321, 'Only converting DNA into images', 0),
(1285, 322, 'Alignment of two biological sequences', 1),
(1286, 322, 'Alignment of exactly ten sequences', 0),
(1287, 322, 'A database backup method', 0),
(1288, 322, 'A protein-folding experiment only', 0),
(1289, 323, 'Alignment of three or more sequences', 1),
(1290, 323, 'Alignment of only one sequence', 0),
(1291, 323, 'Deleting duplicated database rows', 0),
(1292, 323, 'Measuring file size', 0),
(1293, 324, 'Sequences similar to a query sequence', 1),
(1294, 324, 'Only duplicate filenames', 0),
(1295, 324, 'Only protein colors', 0),
(1296, 324, 'Only memory usage', 0),
(1297, 325, 'Possible biological or evolutionary relationships', 1),
(1298, 325, 'The exact age of every organism', 0),
(1299, 325, 'That two organisms are always identical', 0),
(1300, 325, 'That the sequences must have the same length', 0),
(1301, 326, 'A possible insertion or deletion', 1),
(1302, 326, 'A database password', 0),
(1303, 326, 'A protein color', 0),
(1304, 326, 'A file extension', 0),
(1305, 327, 'The same symbol or residue aligned at corresponding positions', 1),
(1306, 327, 'A missing database entry', 0),
(1307, 327, 'An empty sequence', 0),
(1308, 327, 'An invalid file format', 0),
(1309, 328, 'Different residues aligned at corresponding positions', 1),
(1310, 328, 'Two identical residues', 0),
(1311, 328, 'A completely empty database', 0),
(1312, 328, 'A successful compilation', 0),
(1313, 329, 'Clustal Omega', 1),
(1314, 329, 'Microsoft Paint', 0),
(1315, 329, 'MySQL Workbench', 0),
(1316, 329, 'Android Studio', 0),
(1317, 330, 'To find related sequences and possible functional clues', 1),
(1318, 330, 'To delete the database', 0),
(1319, 330, 'To compile the sequence into an application', 0),
(1320, 330, 'To convert the sequence into an operating system', 0),
(1321, 331, 'Count the number of nucleotide characters', 1),
(1322, 331, 'Count only the G characters', 0),
(1323, 331, 'Count only spaces', 0),
(1324, 331, 'Use the database row ID', 0),
(1325, 332, 'The number of times each nucleotide occurs in a sequence', 1),
(1326, 332, 'The physical size of a chromosome', 0),
(1327, 332, 'The number of files in a folder', 0),
(1328, 332, 'The sequence database password', 0),
(1329, 333, 'The proportion of G and C nucleotides in a DNA sequence', 1),
(1330, 333, 'Only the number of adenines', 0),
(1331, 333, 'Only the sequence length', 0),
(1332, 333, 'The number of proteins in a cell', 0),
(1333, 334, 'A, T, G, and C', 1),
(1334, 334, 'A, B, C, and D', 0),
(1335, 334, '1, 2, 3, and 4', 0),
(1336, 334, 'X, Y, Z, and W', 0),
(1337, 335, 'Report or handle the invalid input instead of silently treating it as valid DNA', 1),
(1338, 335, 'Always count it as G', 0),
(1339, 335, 'Always delete the entire program', 0),
(1340, 335, 'Always change it to a number', 0),
(1341, 336, 'A loop', 1),
(1342, 336, 'A comment only', 0),
(1343, 336, 'A file extension', 0),
(1344, 336, 'A package name only', 0),
(1345, 337, 'A dictionary or map', 1),
(1346, 337, 'A single Boolean only', 0),
(1347, 337, 'A comment block', 0),
(1348, 337, 'A null value only', 0),
(1349, 338, 'Calculations such as percentages may otherwise be invalid or meaningless', 1),
(1350, 338, 'Empty sequences are always valid proteins', 0),
(1351, 338, 'It guarantees a GC content of 100 percent', 0),
(1352, 338, 'It automatically creates nucleotide data', 0),
(1353, 339, 'Convert the sequence to uppercase', 1),
(1354, 339, 'Delete every nucleotide', 0),
(1355, 339, 'Convert the sequence to an integer', 0),
(1356, 339, 'Replace every nucleotide with a space', 0),
(1357, 340, 'They automate repetitive calculations over biological data', 1),
(1358, 340, 'They eliminate the need for biological interpretation', 0),
(1359, 340, 'They prevent data from being stored', 0),
(1360, 340, 'They make every sequence identical', 0),
(1361, 341, 'That the sequence contains valid nucleotide characters', 1),
(1362, 341, 'That the sequence contains only numbers', 0),
(1363, 341, 'That every character is a space', 0),
(1364, 341, 'That the sequence is always empty', 0),
(1365, 342, 'Nucleotide frequency analysis', 1),
(1366, 342, 'Image analysis', 0),
(1367, 342, 'File compression', 0),
(1368, 342, 'Database deletion', 0),
(1369, 343, 'G and C', 1),
(1370, 343, 'A and T only', 0),
(1371, 343, 'A and G only', 0),
(1372, 343, 'T and C only', 0),
(1373, 344, 'It has no sequence information and may cause invalid percentage calculations', 1),
(1374, 344, 'It always represents a complete genome', 0),
(1375, 344, 'It always has 100 percent GC content', 0),
(1376, 344, 'It automatically contains all four nucleotides', 0),
(1377, 345, 'Functions', 1),
(1378, 345, 'Comments only', 0),
(1379, 345, 'File extensions', 0),
(1380, 345, 'Database names only', 0),
(1381, 346, 'They provide basic descriptive information about the sequence', 1),
(1382, 346, 'They guarantee the sequence belongs to a specific species', 0),
(1383, 346, 'They determine every protein function automatically', 0),
(1384, 346, 'They remove the need for validation', 0),
(1385, 347, 'The basic DNA validator should flag it as invalid', 1),
(1386, 347, 'It should always be counted as adenine', 0),
(1387, 347, 'It should always be counted as cytosine', 0),
(1388, 347, 'It should automatically become a protein', 0),
(1389, 348, 'To verify calculations and edge-case handling', 1),
(1390, 348, 'To make all sequences identical', 0),
(1391, 348, 'To remove all validation rules', 0),
(1392, 348, 'To stop the program from accepting input', 0),
(1393, 349, 'A percentage such as GC content', 1),
(1394, 349, 'Only the filename', 0),
(1395, 349, 'Only the database ID', 0),
(1396, 349, 'Only the number of comments', 0),
(1397, 350, 'Test valid inputs, invalid inputs, and the correctness of calculated results', 1),
(1398, 350, 'Remove all validation', 0),
(1399, 350, 'Never run the program', 0),
(1400, 350, 'Delete the analysis functions', 0);

-- --------------------------------------------------------

--
-- Table structure for table `quiz_questions`
--

CREATE TABLE `quiz_questions` (
  `id` int(11) NOT NULL,
  `session_id` int(11) NOT NULL,
  `question` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `quiz_questions`
--

INSERT INTO `quiz_questions` (`id`, `session_id`, `question`) VALUES
(1, 1, 'What is Python?'),
(2, 1, 'Which function is commonly used to display output in Python?'),
(3, 1, 'Which symbol is used to create a comment in Python?'),
(4, 1, 'Which file extension is normally used for Python files?'),
(5, 1, 'Which of the following is a valid Python variable name?'),
(6, 1, 'Which keyword is used to define a function in Python?'),
(7, 1, 'Python is considered which type of programming language?'),
(8, 1, 'Which function can be used to receive input from a user?'),
(9, 1, 'Which of the following represents a string in Python?'),
(10, 1, 'Which statement about Python indentation is correct?'),
(11, 2, 'Which operator is used to assign a value to a variable in Python?'),
(12, 2, 'Which data type is used for whole numbers in Python?'),
(13, 2, 'Which data type represents decimal numbers?'),
(14, 2, 'What is the data type of the value \"CodeLearn\"?'),
(15, 2, 'Which value is a Boolean value in Python?'),
(16, 2, 'Which function can be used to check the type of a value?'),
(17, 2, 'What is the result of int(\"10\")?'),
(18, 2, 'Which variable name is valid in Python?'),
(19, 2, 'What is the value of x after x = 5 + 3?'),
(20, 2, 'Which function converts a value to a string?'),
(21, 3, 'Which keyword starts a conditional statement in Python?'),
(22, 3, 'Which keyword is used when the previous if condition is false and another condition must be checked?'),
(23, 3, 'Which keyword handles the case when all previous conditions are false?'),
(24, 3, 'Which loop is commonly used to iterate through a sequence?'),
(25, 3, 'A while loop continues running while what is true?'),
(26, 3, 'Which statement immediately exits a loop?'),
(27, 3, 'Which statement skips the current loop iteration and continues with the next one?'),
(28, 3, 'What values are produced by range(3)?'),
(29, 3, 'Which operator checks whether two values are equal?'),
(30, 3, 'What does the condition x > 5 check?'),
(31, 4, 'Which keyword is used to create a function in Python?'),
(32, 4, 'Which keyword sends a value back from a function?'),
(33, 4, 'Which collection uses square brackets?'),
(34, 4, 'Which collection is normally written using parentheses?'),
(35, 4, 'Which collection stores key-value pairs?'),
(36, 4, 'Which list method adds an item to the end of a list?'),
(37, 4, 'Which function returns the number of items in a collection?'),
(38, 4, 'What is the first index of a Python list?'),
(39, 4, 'Which collection automatically stores unique values?'),
(40, 4, 'What is a function parameter?'),
(41, 5, 'Which Python construct is best used to make reusable blocks of code?'),
(42, 5, 'Which statement is useful for making decisions in a program?'),
(43, 5, 'Which structure is appropriate for storing multiple ordered values?'),
(44, 5, 'Which operator checks equality?'),
(45, 5, 'Which keyword is used to repeat code over elements in a sequence?'),
(46, 5, 'Which function is used to receive keyboard input?'),
(47, 5, 'Which keyword is used to return a result from a function?'),
(48, 5, 'What is the purpose of indentation in Python?'),
(49, 5, 'Which data type represents True or False?'),
(50, 5, 'What is a good first step when building a small Python project?'),
(51, 6, 'What is C?'),
(52, 6, 'Which function is commonly used to display output in C?'),
(53, 6, 'Which header is commonly used for printf()?'),
(54, 6, 'Which function normally serves as the starting point of a C program?'),
(55, 6, 'Which character normally ends a C statement?'),
(56, 6, 'Which syntax creates a single-line comment in C?'),
(57, 6, 'Which file extension is commonly used for C source files?'),
(58, 6, 'What does return 0 from main commonly indicate?'),
(59, 6, 'C source code is normally translated into machine code using a what?'),
(60, 6, 'Which symbols define a block of code in C?'),
(61, 7, 'Which type is commonly used for whole numbers in C?'),
(62, 7, 'Which type is commonly used for decimal values?'),
(63, 7, 'Which type stores a single character?'),
(64, 7, 'Which is a valid C variable declaration?'),
(65, 7, 'Which operator assigns a value to a variable?'),
(66, 7, 'Which printf format specifier is typically used for int?'),
(67, 7, 'Which printf format specifier is used for a character?'),
(68, 7, 'Which function is commonly used to read formatted input in C?'),
(69, 7, 'What is the result of int x = 5 + 3?'),
(70, 7, 'Which keyword can make a variable value not modifiable through that identifier?'),
(71, 8, 'Which keyword begins a conditional statement in C?'),
(72, 8, 'Which operator tests equality?'),
(73, 8, 'Which keyword executes when an if condition is false?'),
(74, 8, 'Which loop is useful when the number of iterations is known?'),
(75, 8, 'Which loop checks its condition before executing its body?'),
(76, 8, 'Which loop executes its body at least once?'),
(77, 8, 'Which statement exits a loop immediately?'),
(78, 8, 'Which statement skips to the next iteration of a loop?'),
(79, 8, 'Which operator represents logical AND in C?'),
(80, 8, 'Which statement is useful when selecting one of several constant cases?'),
(81, 9, 'What is the main purpose of a function?'),
(82, 9, 'Which keyword indicates that a function returns no value?'),
(83, 9, 'Which statement sends a value back from a function?'),
(84, 9, 'Which declaration creates an array of five integers?'),
(85, 9, 'What is the first index of an array in C?'),
(86, 9, 'For int values[5], what is the final valid index?'),
(87, 9, 'What is a function parameter?'),
(88, 9, 'Which syntax accesses the third item of an array named values?'),
(89, 9, 'Which type of values must a normal C array contain?'),
(90, 9, 'What does calling a function mean?'),
(91, 10, 'Which construct should be used to store several book records of the same type?'),
(92, 10, 'Which feature helps divide a large C program into reusable operations?'),
(93, 10, 'Which keyword creates a custom grouped data structure in C?'),
(94, 10, 'Which control structure can prevent borrowing when stock is zero?'),
(95, 10, 'Which loop can process every item in an array?'),
(96, 10, 'Which function is normally used to display formatted information?'),
(97, 10, 'Why should user input be validated?'),
(98, 10, 'Which operator is commonly used to increase a variable by one?'),
(99, 10, 'What should happen when a requested book is unavailable?'),
(100, 10, 'What is a good practice before considering a program complete?'),
(101, 11, 'What is Java?'),
(102, 11, 'Which method is the usual entry point of a Java application?'),
(103, 11, 'Which statement prints text followed by a new line in Java?'),
(104, 11, 'Which file extension is normally used for Java source files?'),
(105, 11, 'Which file commonly contains Java bytecode after compilation?'),
(106, 11, 'What does JVM stand for?'),
(107, 11, 'Which keyword is used to declare a class?'),
(108, 11, 'Which symbol normally terminates a Java statement?'),
(109, 11, 'Which syntax creates a single-line comment in Java?'),
(110, 11, 'Which command is commonly used to compile a Java source file?'),
(111, 12, 'Which Java primitive type is commonly used for whole numbers?'),
(112, 12, 'Which primitive type stores true or false values?'),
(113, 12, 'Which primitive type stores a single Unicode character?'),
(114, 12, 'Which type is commonly used for decimal numbers with double precision?'),
(115, 12, 'Which is a valid Java variable declaration?'),
(116, 12, 'Which operator assigns a value to a variable?'),
(117, 12, 'Which class represents text strings in Java?'),
(118, 12, 'Which keyword prevents a variable from being reassigned after initialization when used appropriately?'),
(119, 12, 'What is the result of int x = 5 + 3;?'),
(120, 12, 'Which is one of the eight primitive types in Java?'),
(121, 13, 'Which keyword begins a conditional statement in Java?'),
(122, 13, 'Which keyword provides an alternative when an if condition is false?'),
(123, 13, 'Which operator checks equality for primitive values?'),
(124, 13, 'Which loop is commonly used when the number of iterations is known?'),
(125, 13, 'Which loop continues while its condition evaluates to true?'),
(126, 13, 'Which loop executes its body at least once before testing its condition?'),
(127, 13, 'Which statement immediately exits the current loop?'),
(128, 13, 'Which statement skips the rest of the current loop iteration?'),
(129, 13, 'Which operator represents logical AND?'),
(130, 13, 'Which construct can select among several alternatives based on a value?'),
(131, 14, 'What is a class in Java?'),
(132, 14, 'Which keyword is used to create a new object?'),
(133, 14, 'What is a method?'),
(134, 14, 'What is the purpose of a constructor?'),
(135, 14, 'Which access modifier limits a member to its own class?'),
(136, 14, 'Which keyword refers to the current object inside an instance method or constructor?'),
(137, 14, 'Which keyword indicates that a method returns no value?'),
(138, 14, 'What is a parameter?'),
(139, 14, 'Which syntax accesses a public method named start on an object named car?'),
(140, 14, 'Which type of member belongs to the class rather than each individual object when declared with the appropriate keyword?'),
(141, 15, 'Which Java feature is useful for representing vehicles as objects with data and behavior?'),
(142, 15, 'Which structure is suitable for storing several values of the same type with a fixed length?'),
(143, 15, 'What is the first index of a Java array?'),
(144, 15, 'Which control structure can reject a booking when no vehicle is available?'),
(145, 15, 'Which concept helps keep fields hidden and controlled through methods?'),
(146, 15, 'Which method characteristic lets a method send a value back to its caller?'),
(147, 15, 'Which construct is appropriate for repeatedly processing multiple bookings?'),
(148, 15, 'Why should invalid passenger counts be checked?'),
(149, 15, 'Which keyword can be used to create an object from a class?'),
(150, 15, 'What is a good final step before considering the Java project complete?'),
(151, 16, 'What is JavaScript commonly used for?'),
(152, 16, 'Which function can display information in the browser developer console?'),
(153, 16, 'Which file extension is commonly used for JavaScript files?'),
(154, 16, 'Which syntax creates a single-line comment in JavaScript?'),
(155, 16, 'Which statement can be used to declare a block-scoped variable?'),
(156, 16, 'Which keyword declares a binding that cannot be reassigned?'),
(157, 16, 'Is JavaScript case-sensitive?'),
(158, 16, 'Which syntax represents a string literal?'),
(159, 16, 'Which keyword was historically used to declare variables before let and const were introduced?'),
(160, 16, 'Which organization standardizes the ECMAScript specification used by JavaScript?'),
(161, 17, 'Which JavaScript type represents true or false values?'),
(162, 17, 'Which JavaScript type is commonly used for numeric values?'),
(163, 17, 'Which represents a string value?'),
(164, 17, 'Which operator assigns a value to a variable?'),
(165, 17, 'Which operator returns the type of a value as a string?'),
(166, 17, 'Which value represents the intentional absence of an object value?'),
(167, 17, 'What does undefined commonly mean?'),
(168, 17, 'Which declaration is valid JavaScript?'),
(169, 17, 'What is the result of 5 + 3 when both operands are numbers?'),
(170, 17, 'JavaScript is described as dynamically typed because what can happen?'),
(171, 18, 'Which keyword begins a conditional statement?'),
(172, 18, 'Which keyword provides an alternative when an if condition is false?'),
(173, 18, 'Which operator performs strict equality comparison?'),
(174, 18, 'Which loop is commonly used when repeating code a known number of times?'),
(175, 18, 'Which loop continues while its condition remains true?'),
(176, 18, 'Which loop executes its body at least once?'),
(177, 18, 'Which statement exits a loop immediately?'),
(178, 18, 'Which statement skips the rest of the current loop iteration?'),
(179, 18, 'Which operator represents logical AND?'),
(180, 18, 'Which statement can select among several branches based on a value?'),
(181, 19, 'What is a function primarily used for?'),
(182, 19, 'Which keyword is commonly used in a function declaration?'),
(183, 19, 'Which keyword sends a value back from a function?'),
(184, 19, 'Which syntax creates an array literal?'),
(185, 19, 'What is the first index of a JavaScript array?'),
(186, 19, 'Which array method adds one or more elements to the end of an array?'),
(187, 19, 'Which array method removes the final element?'),
(188, 19, 'Which property gives the number of elements in an array?'),
(189, 19, 'Which syntax is an arrow function?'),
(190, 19, 'What is a function parameter?'),
(191, 20, 'Which collection is suitable for storing multiple participant names in order?'),
(192, 20, 'Which feature helps organize reusable participant-management logic?'),
(193, 20, 'Which array method can add a new participant to the end of an array?'),
(194, 20, 'Which condition can be used to prevent adding an empty participant name?'),
(195, 20, 'Which loop can iterate over all participants in an array?'),
(196, 20, 'Which comparison operator checks strict equality?'),
(197, 20, 'Which built-in method can determine whether an array contains a specific primitive value?'),
(198, 20, 'Why should duplicate participant names be checked?'),
(199, 20, 'What is a useful way to separate event-management operations?'),
(200, 20, 'What should be done before considering the final JavaScript project complete?'),
(201, 21, 'What is a data structure?'),
(202, 21, 'Which data structure stores elements in indexed positions?'),
(203, 21, 'What is the typical first index of an array in many programming languages?'),
(204, 21, 'What does Big-O notation describe?'),
(205, 21, 'What does O(1) commonly represent?'),
(206, 21, 'What does O(n) commonly represent?'),
(207, 21, 'Which operation is typically constant time for direct array access by index?'),
(208, 21, 'What is one advantage of arrays?'),
(209, 21, 'Which complexity generally grows faster than O(n) as n becomes large?'),
(210, 21, 'Why is complexity analysis useful?'),
(211, 22, 'What is a linked list made of?'),
(212, 22, 'What does a node in a singly linked list usually contain?'),
(213, 22, 'What is the first node of a linked list commonly called?'),
(214, 22, 'What usually marks the end of a singly linked list?'),
(215, 22, 'Which linked-list operation can be efficient at the beginning of the list?'),
(216, 22, 'What is a key difference between an array and a linked list?'),
(217, 22, 'What additional reference does a doubly linked list node normally have?'),
(218, 22, 'Why can random access be slower in a linked list than in an array?'),
(219, 22, 'What operation removes a node from a linked list?'),
(220, 22, 'Which structure can be implemented using a linked list?'),
(221, 23, 'Which principle does a stack follow?'),
(222, 23, 'Which operation adds an element to a stack?'),
(223, 23, 'Which operation removes the top element from a stack?'),
(224, 23, 'Which principle does a queue follow?'),
(225, 23, 'Which queue operation adds an element to the rear?'),
(226, 23, 'Which queue operation removes an element from the front?'),
(227, 23, 'What does peek usually do on a stack?'),
(228, 23, 'Which real-world example closely resembles a queue?'),
(229, 23, 'Which real-world example closely resembles a stack?'),
(230, 23, 'What should be checked before popping from an empty stack?'),
(231, 24, 'What type of structure is a tree?'),
(232, 24, 'What is the topmost node of a tree called?'),
(233, 24, 'What is a leaf node?'),
(234, 24, 'What is a binary tree?'),
(235, 24, 'What are the main components of a graph?'),
(236, 24, 'What is an edge in a graph?'),
(237, 24, 'Which representation uses a two-dimensional matrix to represent graph connections?'),
(238, 24, 'Which representation stores neighbors for each graph vertex?'),
(239, 24, 'Which traversal explores a graph level by level?'),
(240, 24, 'Which traversal explores deeply along a path before backtracking?'),
(241, 25, 'Which data structure is suitable for implementing browser back-history behavior?'),
(242, 25, 'Which structure is suitable for processing requests in arrival order?'),
(243, 25, 'Which structure is appropriate for representing relationships between cities connected by roads?'),
(244, 25, 'Which data structure naturally represents hierarchical organization?'),
(245, 25, 'Which structure is appropriate when frequent insertions at the beginning are needed without shifting array elements?'),
(246, 25, 'What should influence the choice of a data structure?'),
(247, 25, 'Which operation should be handled safely when removing from an empty stack?'),
(248, 25, 'Which problem is naturally modeled with FIFO behavior?'),
(249, 25, 'Why is testing edge cases important in a data-structure project?'),
(250, 25, 'What is a good final step when completing the Data Structures case study?'),
(251, 26, 'What is a class in object-oriented programming?'),
(252, 26, 'What is an object?'),
(253, 26, 'What normally represents the state of an object?'),
(254, 26, 'What normally represents the behavior of an object?'),
(255, 26, 'Which Java keyword creates a new object?'),
(256, 26, 'What is a constructor used for?'),
(257, 26, 'Which operator is commonly used to access an object member in Java?'),
(258, 26, 'If Car is a class, what is car1 in Car car1 = new Car();?'),
(259, 26, 'Why are classes useful?'),
(260, 26, 'Which statement best describes multiple objects created from one class?'),
(261, 27, 'What is encapsulation?'),
(262, 27, 'Which access modifier is commonly used to hide a field from other classes?'),
(263, 27, 'What is a getter commonly used for?'),
(264, 27, 'What is a setter commonly used for?'),
(265, 27, 'Why can setters perform validation?'),
(266, 27, 'Which design best supports encapsulation?'),
(267, 27, 'What is information hiding?'),
(268, 27, 'Why is direct unrestricted modification of important fields often avoided?'),
(269, 27, 'Which OOP principle helps an object remain responsible for the validity of its own data?'),
(270, 27, 'What is one benefit of encapsulation?'),
(271, 28, 'What is inheritance?'),
(272, 28, 'What is a superclass?'),
(273, 28, 'What is a subclass?'),
(274, 28, 'Which Java keyword is used to inherit from a class?'),
(275, 28, 'What is a major benefit of inheritance?'),
(276, 28, 'Which relationship usually describes inheritance?'),
(277, 28, 'What does method overriding allow?'),
(278, 28, 'Which Java keyword can refer to superclass members or constructors?'),
(279, 28, 'Can a normal Java class directly extend two different classes?'),
(280, 28, 'What is inherited by a subclass from its superclass, subject to access rules?'),
(281, 29, 'What is polymorphism?'),
(282, 29, 'Which feature commonly enables runtime polymorphism in Java?'),
(283, 29, 'What is abstraction?'),
(284, 29, 'Which Java keyword declares an abstract class or method?'),
(285, 29, 'Can an abstract class normally be instantiated directly?'),
(286, 29, 'What does an interface commonly define?'),
(287, 29, 'Which keyword connects a Java class to an interface?'),
(288, 29, 'What happens when an overridden method is called through a superclass reference pointing to a subclass object?'),
(289, 29, 'Which OOP concept reduces dependence on implementation details?'),
(290, 29, 'Which combination is central to flexible OOP design?'),
(291, 30, 'Which OOP principle should be used to hide student fields from direct modification?'),
(292, 30, 'Which OOP principle can model UndergraduateStudent and GraduateStudent as specialized Student types?'),
(293, 30, 'Which concept allows different student types to implement the same operation differently?'),
(294, 30, 'Which concept can define only the essential operations required from student types?'),
(295, 30, 'Why should validation logic be placed inside appropriate object methods?'),
(296, 30, 'Which relationship best describes a Student containing an Address object?'),
(297, 30, 'Why is code reuse important in OOP design?'),
(298, 30, 'Which design is generally better for important internal student data?'),
(299, 30, 'What should be checked when designing a class hierarchy?'),
(300, 30, 'What should be done before submitting the OOP Final Project for mentor review?'),
(301, 31, 'What does computational biology combine?'),
(302, 31, 'What is bioinformatics commonly concerned with?'),
(303, 31, 'Which field commonly provides biological sequence data?'),
(304, 31, 'Which molecule stores hereditary information in most organisms?'),
(305, 31, 'Which molecule is produced from DNA during transcription?'),
(306, 31, 'Proteins are built from which basic units?'),
(307, 31, 'Why are computers useful in modern biology?'),
(308, 31, 'Which is an example of a computational biology task?'),
(309, 31, 'What is a genome?'),
(310, 31, 'Which skill is useful in computational biology?'),
(311, 32, 'Which letters are used to represent the standard DNA nucleotides?'),
(312, 32, 'Which nucleotide is normally found in RNA instead of thymine?'),
(313, 32, 'What does a DNA sequence represent?'),
(314, 32, 'What does a protein sequence represent?'),
(315, 32, 'What is FASTA commonly used to represent?'),
(316, 32, 'What commonly begins a FASTA header line?'),
(317, 32, 'What is a biological database used for?'),
(318, 32, 'Which type of data can be stored in sequence databases?'),
(319, 32, 'Why is biological data validation important?'),
(320, 32, 'Which data type could represent a DNA sequence in a program?'),
(321, 33, 'What is sequence alignment used for?'),
(322, 33, 'What is pairwise sequence alignment?'),
(323, 33, 'What is multiple sequence alignment?'),
(324, 33, 'What does BLAST help identify?'),
(325, 33, 'What can sequence similarity suggest?'),
(326, 33, 'What is a gap in a sequence alignment commonly used to represent?'),
(327, 33, 'What is a match in a sequence alignment?'),
(328, 33, 'What is a mismatch in an alignment?'),
(329, 33, 'Which tool is widely used for multiple sequence alignment?'),
(330, 33, 'Why might a researcher search a sequence database with a query sequence?'),
(331, 34, 'How can the length of a DNA sequence stored as a string be calculated?'),
(332, 34, 'What is nucleotide frequency?'),
(333, 34, 'What does GC content measure?'),
(334, 34, 'Which characters should a simple DNA validator normally accept?'),
(335, 34, 'What should a program do if an invalid nucleotide character is found?'),
(336, 34, 'Which programming construct is useful for counting each nucleotide in a sequence?'),
(337, 34, 'Which structure can conveniently store counts for A, T, G, and C?'),
(338, 34, 'Why should an empty DNA sequence be handled as an edge case?'),
(339, 34, 'Which operation could be used to convert lowercase DNA input to a consistent form?'),
(340, 34, 'Why are simple computational methods useful in biological sequence analysis?'),
(341, 35, 'What should be validated before analyzing a DNA sequence?'),
(342, 35, 'Which analysis reports how many A, T, G, and C symbols occur?'),
(343, 35, 'Which nucleotides are included when calculating GC content?'),
(344, 35, 'Why should an empty input be rejected or handled specially?'),
(345, 35, 'Which programming idea helps avoid repeating sequence-analysis code?'),
(346, 35, 'What is the benefit of reporting both sequence length and nucleotide counts?'),
(347, 35, 'What should happen if the input contains the character X?'),
(348, 35, 'Why should the DNA analyzer be tested with different sequences?'),
(349, 35, 'Which result is useful for comparing nucleotide composition between sequences of different lengths?'),
(350, 35, 'What should be done before submitting the Computational Biology Final Project?');

-- --------------------------------------------------------

--
-- Table structure for table `sessions`
--

CREATE TABLE `sessions` (
  `id` int(11) NOT NULL,
  `course_id` int(11) NOT NULL,
  `session_number` int(11) NOT NULL,
  `title` varchar(150) NOT NULL,
  `description` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `sessions`
--

INSERT INTO `sessions` (`id`, `course_id`, `session_number`, `title`, `description`) VALUES
(1, 1, 1, 'Introduction to Python', 'Introduction, syntax, and basic output.'),
(2, 1, 2, 'Variables and Data Types', 'Variables, numbers, strings, and basic operations.'),
(3, 1, 3, 'Conditionals and Loops', 'If statements, for loops, and while loops.'),
(4, 1, 4, 'Functions and Collections', 'Functions, lists, tuples, and dictionaries.'),
(5, 1, 5, 'Final Session', 'Review and final case study preparation.'),
(6, 2, 1, 'Introduction to C', 'Basic syntax and program structure.'),
(7, 2, 2, 'Variables and Data Types', 'Variables, primitive types, and operators.'),
(8, 2, 3, 'Conditionals and Loops', 'Branching and repetition.'),
(9, 2, 4, 'Functions and Arrays', 'Functions, arrays, and basic modular programming.'),
(10, 2, 5, 'Final Session', 'Review and final case study preparation.'),
(11, 3, 1, 'Introduction to Java', 'Basic Java syntax and program structure.'),
(12, 3, 2, 'Variables and Data Types', 'Variables, types, and operators.'),
(13, 3, 3, 'Conditionals and Loops', 'Control flow in Java.'),
(14, 3, 4, 'Methods and Classes', 'Methods, classes, and objects.'),
(15, 3, 5, 'Final Session', 'Review and final case study preparation.'),
(16, 4, 1, 'Introduction to JavaScript', 'Basic syntax and browser scripting concepts.'),
(17, 4, 2, 'Variables and Data Types', 'let, const, primitive values, and operators.'),
(18, 4, 3, 'Conditionals and Loops', 'Decision making and iteration.'),
(19, 4, 4, 'Functions and Arrays', 'Functions, arrays, and objects.'),
(20, 4, 5, 'Final Session', 'Review and final case study preparation.'),
(21, 5, 1, 'Arrays and Complexity', 'Arrays and basic complexity concepts.'),
(22, 5, 2, 'Linked Lists', 'Singly and doubly linked lists.'),
(23, 5, 3, 'Stacks and Queues', 'LIFO and FIFO structures.'),
(24, 5, 4, 'Trees and Graphs', 'Basic tree and graph structures.'),
(25, 5, 5, 'Final Session', 'Review and final case study preparation.'),
(26, 6, 1, 'Classes and Objects', 'Introduction to object-oriented programming.'),
(27, 6, 2, 'Encapsulation', 'Data hiding and access control.'),
(28, 6, 3, 'Inheritance', 'Reusing behavior through inheritance.'),
(29, 6, 4, 'Polymorphism and Abstraction', 'Core OOP design concepts.'),
(30, 6, 5, 'Final Session', 'Review and final case study preparation.'),
(31, 7, 1, 'Introduction to Computational Biology', 'Introduction to computational approaches in biology.'),
(32, 7, 2, 'Biological Data', 'Basic biological datasets and representations.'),
(33, 7, 3, 'Sequence Analysis Basics', 'Introduction to DNA and protein sequence analysis.'),
(34, 7, 4, 'Simple Computational Methods', 'Basic computational techniques for biological data.'),
(35, 7, 5, 'Final Session', 'Review and final case study preparation.');

-- --------------------------------------------------------

--
-- Table structure for table `session_progress`
--

CREATE TABLE `session_progress` (
  `id` int(11) NOT NULL,
  `student_id` int(11) NOT NULL,
  `session_id` int(11) NOT NULL,
  `started_at` datetime DEFAULT current_timestamp(),
  `deadline_at` datetime NOT NULL,
  `completed_at` datetime DEFAULT NULL,
  `status` enum('active','passed','expired') DEFAULT 'active',
  `quiz_score` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `session_progress`
--

INSERT INTO `session_progress` (`id`, `student_id`, `session_id`, `started_at`, `deadline_at`, `completed_at`, `status`, `quiz_score`) VALUES
(1, 1, 1, '2026-11-15 10:46:09', '2026-11-22 10:46:09', '2026-11-16 18:00:00', 'passed', 100),
(2, 1, 2, '2026-11-15 12:11:02', '2026-11-22 12:11:02', NULL, 'active', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `student_certificates`
--

CREATE TABLE `student_certificates` (
  `id` int(11) NOT NULL,
  `student_id` int(11) NOT NULL,
  `mentor_id` int(11) NOT NULL,
  `course_id` int(11) NOT NULL,
  `review_id` int(11) NOT NULL,
  `certificate_id` varchar(100) NOT NULL,
  `pdf_url` text DEFAULT NULL,
  `claimed_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `student_course_progress`
--

CREATE TABLE `student_course_progress` (
  `id` int(11) NOT NULL,
  `student_id` int(11) NOT NULL,
  `course_id` int(11) NOT NULL,
  `current_session` int(11) DEFAULT 1,
  `started_at` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `student_course_progress`
--

INSERT INTO `student_course_progress` (`id`, `student_id`, `course_id`, `current_session`, `started_at`) VALUES
(1, 1, 1, 2, '2026-11-15 10:46:09');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `role` enum('student','mentor') NOT NULL,
  `name` varchar(100) NOT NULL,
  `campus` varchar(150) DEFAULT NULL,
  `profile_image` varchar(255) DEFAULT NULL,
  `cover_image` varchar(255) DEFAULT NULL,
  `is_demo` tinyint(1) DEFAULT 0,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `email`, `password_hash`, `role`, `name`, `campus`, `profile_image`, `cover_image`, `is_demo`, `created_at`) VALUES
(1, 'studentdemo@codelearn.com', '$2b$10$Lyg5vPHo3h1erWECAcEfVuoWfS5K58WnECl/y9sFCcq6BDadjRhO6', 'student', 'Student Demo', 'BINUS University', '/uploads/profiles/profile-images/1-1794737569099.jpg', '/uploads/profiles/cover-images/1-1794738252562.jpg', 1, '2026-11-14 08:12:26'),
(2, 'mentordemo@codelearn.com', '$2b$10$.BA9pOBkLKJmXHUayq3EnuZLhrQZ1QkoMWDv2iLu36LUrUZY2KwfS', 'mentor', 'Mentor Demo', 'BINUS Alam Sutera', '/uploads/profiles/profile-images/2-1794744056013.jpg', NULL, 0, '2026-11-15 11:55:05');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `case_studies`
--
ALTER TABLE `case_studies`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `course_id` (`course_id`);

--
-- Indexes for table `case_study_reviews`
--
ALTER TABLE `case_study_reviews`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `mentoring_id` (`mentoring_id`),
  ADD KEY `case_study_id` (`case_study_id`);

--
-- Indexes for table `courses`
--
ALTER TABLE `courses`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `materials`
--
ALTER TABLE `materials`
  ADD PRIMARY KEY (`id`),
  ADD KEY `session_id` (`session_id`);

--
-- Indexes for table `mentorings`
--
ALTER TABLE `mentorings`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `request_id` (`request_id`),
  ADD KEY `student_id` (`student_id`),
  ADD KEY `mentor_id` (`mentor_id`),
  ADD KEY `course_id` (`course_id`);

--
-- Indexes for table `mentoring_requests`
--
ALTER TABLE `mentoring_requests`
  ADD PRIMARY KEY (`id`),
  ADD KEY `student_id` (`student_id`),
  ADD KEY `mentor_id` (`mentor_id`),
  ADD KEY `course_id` (`course_id`);

--
-- Indexes for table `mentor_certificates`
--
ALTER TABLE `mentor_certificates`
  ADD PRIMARY KEY (`id`),
  ADD KEY `mentor_id` (`mentor_id`);

--
-- Indexes for table `mentor_specializations`
--
ALTER TABLE `mentor_specializations`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_mentor_course` (`mentor_id`,`course_id`),
  ADD KEY `fk_mentor_specialization_course` (`course_id`);

--
-- Indexes for table `messages`
--
ALTER TABLE `messages`
  ADD PRIMARY KEY (`id`),
  ADD KEY `mentoring_id` (`mentoring_id`),
  ADD KEY `sender_id` (`sender_id`);

--
-- Indexes for table `notifications`
--
ALTER TABLE `notifications`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `quiz_answers`
--
ALTER TABLE `quiz_answers`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_student_session_question` (`student_id`,`session_id`,`question_id`),
  ADD KEY `session_id` (`session_id`),
  ADD KEY `question_id` (`question_id`),
  ADD KEY `option_id` (`option_id`);

--
-- Indexes for table `quiz_options`
--
ALTER TABLE `quiz_options`
  ADD PRIMARY KEY (`id`),
  ADD KEY `question_id` (`question_id`);

--
-- Indexes for table `quiz_questions`
--
ALTER TABLE `quiz_questions`
  ADD PRIMARY KEY (`id`),
  ADD KEY `session_id` (`session_id`);

--
-- Indexes for table `sessions`
--
ALTER TABLE `sessions`
  ADD PRIMARY KEY (`id`),
  ADD KEY `course_id` (`course_id`);

--
-- Indexes for table `session_progress`
--
ALTER TABLE `session_progress`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `student_id` (`student_id`,`session_id`),
  ADD KEY `session_id` (`session_id`);

--
-- Indexes for table `student_certificates`
--
ALTER TABLE `student_certificates`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `review_id` (`review_id`),
  ADD UNIQUE KEY `certificate_id` (`certificate_id`),
  ADD KEY `student_id` (`student_id`),
  ADD KEY `mentor_id` (`mentor_id`),
  ADD KEY `course_id` (`course_id`);

--
-- Indexes for table `student_course_progress`
--
ALTER TABLE `student_course_progress`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `student_id` (`student_id`,`course_id`),
  ADD KEY `course_id` (`course_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `case_studies`
--
ALTER TABLE `case_studies`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `case_study_reviews`
--
ALTER TABLE `case_study_reviews`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `courses`
--
ALTER TABLE `courses`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `materials`
--
ALTER TABLE `materials`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=106;

--
-- AUTO_INCREMENT for table `mentorings`
--
ALTER TABLE `mentorings`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `mentoring_requests`
--
ALTER TABLE `mentoring_requests`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `mentor_certificates`
--
ALTER TABLE `mentor_certificates`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `mentor_specializations`
--
ALTER TABLE `mentor_specializations`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `messages`
--
ALTER TABLE `messages`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `notifications`
--
ALTER TABLE `notifications`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `quiz_answers`
--
ALTER TABLE `quiz_answers`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `quiz_options`
--
ALTER TABLE `quiz_options`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1401;

--
-- AUTO_INCREMENT for table `quiz_questions`
--
ALTER TABLE `quiz_questions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=351;

--
-- AUTO_INCREMENT for table `sessions`
--
ALTER TABLE `sessions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=36;

--
-- AUTO_INCREMENT for table `session_progress`
--
ALTER TABLE `session_progress`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `student_certificates`
--
ALTER TABLE `student_certificates`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `student_course_progress`
--
ALTER TABLE `student_course_progress`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `case_studies`
--
ALTER TABLE `case_studies`
  ADD CONSTRAINT `case_studies_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `case_study_reviews`
--
ALTER TABLE `case_study_reviews`
  ADD CONSTRAINT `case_study_reviews_ibfk_1` FOREIGN KEY (`mentoring_id`) REFERENCES `mentorings` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `case_study_reviews_ibfk_2` FOREIGN KEY (`case_study_id`) REFERENCES `case_studies` (`id`);

--
-- Constraints for table `materials`
--
ALTER TABLE `materials`
  ADD CONSTRAINT `materials_ibfk_1` FOREIGN KEY (`session_id`) REFERENCES `sessions` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `mentorings`
--
ALTER TABLE `mentorings`
  ADD CONSTRAINT `mentorings_ibfk_1` FOREIGN KEY (`request_id`) REFERENCES `mentoring_requests` (`id`),
  ADD CONSTRAINT `mentorings_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `mentorings_ibfk_3` FOREIGN KEY (`mentor_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `mentorings_ibfk_4` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`);

--
-- Constraints for table `mentoring_requests`
--
ALTER TABLE `mentoring_requests`
  ADD CONSTRAINT `mentoring_requests_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `mentoring_requests_ibfk_2` FOREIGN KEY (`mentor_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `mentoring_requests_ibfk_3` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`);

--
-- Constraints for table `mentor_certificates`
--
ALTER TABLE `mentor_certificates`
  ADD CONSTRAINT `mentor_certificates_ibfk_1` FOREIGN KEY (`mentor_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `mentor_specializations`
--
ALTER TABLE `mentor_specializations`
  ADD CONSTRAINT `fk_mentor_specialization_course` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `mentor_specializations_ibfk_1` FOREIGN KEY (`mentor_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `messages`
--
ALTER TABLE `messages`
  ADD CONSTRAINT `messages_ibfk_1` FOREIGN KEY (`mentoring_id`) REFERENCES `mentorings` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `messages_ibfk_2` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `notifications`
--
ALTER TABLE `notifications`
  ADD CONSTRAINT `notifications_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `quiz_answers`
--
ALTER TABLE `quiz_answers`
  ADD CONSTRAINT `quiz_answers_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `quiz_answers_ibfk_2` FOREIGN KEY (`session_id`) REFERENCES `sessions` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `quiz_answers_ibfk_3` FOREIGN KEY (`question_id`) REFERENCES `quiz_questions` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `quiz_answers_ibfk_4` FOREIGN KEY (`option_id`) REFERENCES `quiz_options` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `quiz_options`
--
ALTER TABLE `quiz_options`
  ADD CONSTRAINT `quiz_options_ibfk_1` FOREIGN KEY (`question_id`) REFERENCES `quiz_questions` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `quiz_questions`
--
ALTER TABLE `quiz_questions`
  ADD CONSTRAINT `quiz_questions_ibfk_1` FOREIGN KEY (`session_id`) REFERENCES `sessions` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `sessions`
--
ALTER TABLE `sessions`
  ADD CONSTRAINT `sessions_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `session_progress`
--
ALTER TABLE `session_progress`
  ADD CONSTRAINT `session_progress_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `session_progress_ibfk_2` FOREIGN KEY (`session_id`) REFERENCES `sessions` (`id`);

--
-- Constraints for table `student_certificates`
--
ALTER TABLE `student_certificates`
  ADD CONSTRAINT `student_certificates_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `student_certificates_ibfk_2` FOREIGN KEY (`mentor_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `student_certificates_ibfk_3` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`),
  ADD CONSTRAINT `student_certificates_ibfk_4` FOREIGN KEY (`review_id`) REFERENCES `case_study_reviews` (`id`);

--
-- Constraints for table `student_course_progress`
--
ALTER TABLE `student_course_progress`
  ADD CONSTRAINT `student_course_progress_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `student_course_progress_ibfk_2` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
