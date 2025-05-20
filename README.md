# Курсовой проект(2 семестр)
##Инфорлогическая модель в нотации Чена 
erDiagram
    STUDENT {
        int student_id PK
        varchar(50) last_name
        varchar(50) first_name
        varchar(50) middle_name
        varchar(200) address
        varchar(20) phone
    }

    ELECTIVE {
        int elective_id PK
        varchar(100) name
        int min_hours_required
        int department_id FK
    }

    ELECTIVE_SEMESTER {
        int elective_semester_id PK
        int elective_id FK
        int semester_id FK
        int lecture_hours
        int practice_hours
        int lab_hours
    }

    SEMESTER {
        int semester_id PK
        int year
        enum('Осенний', 'Весенний') season
    }

    GRADE {
        int grade_id PK
        int student_elective_id FK
        int value
        date date
    }

    DEPARTMENT {
        int department_id PK
        varchar(100) name
    }

    STUDENT ||--o{ GRADE : "получает"
    STUDENT ||--o{ ELECTIVE_SEMESTER : "записан"
    ELECTIVE ||--o{ ELECTIVE_SEMESTER : "включает"
    SEMESTER ||--o{ ELECTIVE_SEMESTER : "проводится в"
    DEPARTMENT ||--o{ ELECTIVE : "организует"
