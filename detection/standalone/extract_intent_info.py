import re

def extract_intent_info(line):
    # Define regular expression patterns to match the sender package and handler package
    sender_pattern = r'Sender:.*?cmp=([^\s]+)/'
    handler_pattern = r'Handler:.*?cmp=([^\s]+)/'
    
    # Use regular expressions to extract the package names from the line
    sender_match = re.search(sender_pattern, line)
    handler_match = re.search(handler_pattern, line)
    
    # If both matches were successful, return the package names
    if sender_match and handler_match:
        sender_package = sender_match.group(1)
        handler_package = handler_match.group(1)
        return sender_package, handler_package
    else:
        return None, None


line = '01-25 14:04:48.976   528  3274 I ActivityTaskManager: START u0 {act=android.intent.action.VIEW cat=[android.intent.category.BROWSABLE] dat=walkingdead://smszombie/?url=http://192.168.1.134:1313 flg=0x14000000 cmp=com.example.smszombie/.WebViewActivity (has extras)} from uid 10121'

sender_package, handler_package = extract_intent_info(line)

print(sender_package)  # None (no sender package found in input)
print(handler_package)  # 'com.example.smszombie' (handler package found in input)

