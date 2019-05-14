SELECT
    cast(
      JSON_ARRAYAGG(JSON_OBJECT('groupId', group_id, 'scheduleEntryId', schedule_entry_id, 'groupNumber', group_number)
    ) as char(400)) as groupIdToEntryIdAndGroupNumber
    , classroom_name, teacher_first_name, teacher_second_name, teacher_middle_name
    , teacher_full_name, subject_type_id, subject_type_abbreviated
    , subject_name, subject_name_abbreviated, week_day, week_parity
    , class_number, class_start_time, class_end_time, class_type
    , subject_id, teacher_id, classroom_id, semester_id
FROM fcimapp.vw_denormalized_regular_schedule
where group_id = ? or teacher_id = ?
group by subject_id, teacher_id, classroom_id, subject_type_id, week_day, class_number, week_parity, semester_id, class_type
