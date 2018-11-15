update social_tenure_relationship set file_number = 'YOUR_CODE/' || right(file_number, length(file_number) - 4) 
where left(file_number, 4) = 'IRD/';