from datetime import datetime
import pandas as pd
import glob


def load_dataset():
  # >===================>
  # load time 
  # >===================>
  dataset = pd.concat(
    [ pd.read_csv(filepath) for filepath in glob.glob(f"./dataset/*.csv") ], 
    ignore_index=True
  )

  # save the combined dataset into one file
  dataset.to_csv('./dataset.csv', index=False)

  # >===================>
  # preprocessing time 
  # >===================>


  dataset['Is_Served'] = dataset['Is_Served'].astype(int)
  dataset['Arrival_Time'] = dataset['Arrival_Time'].apply(convert_to_seconds)
  
  # save preprocessed dataset
  dataset.to_csv('./data.csv', index=False)

  return dataset

def convert_to_seconds(time_str):
  # Reference time for comparison
  reference_time = datetime.strptime('08:00:00', '%H:%M:%S')
  
  try:
      if len(time_str) == 5:
          time_format = '%H:%M'
      elif len(time_str) == 8:
          time_format = '%H:%M:%S'
      else:
          raise ValueError("Invalid time format")
      
      time_obj = datetime.strptime(time_str, time_format)
      return (time_obj - reference_time).seconds
  except ValueError as e:
      print(f"Error processing time: {e}")
      return None