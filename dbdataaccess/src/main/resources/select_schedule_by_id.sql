select
	'dated' as class_type, schedule_entry_id, 0 as cancelled_flg
	, date_key as `date`, schd.class_start_time, schd.class_end_time, schd.class_number
	, classroom_name
	, group_number, subgroup
	, teacher_first_name, teacher_second_name, teacher_middle_name, teacher_full_name
	, subject_type_abbreviated
	, subject_name, subject_name_abbreviated
	, subject_id, group_id, teacher_id
from fcimapp.vw_denormalized_dated_schedule schd
where schd.schedule_entry_id = ?
