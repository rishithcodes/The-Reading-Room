import os
import re

dir_path = "src/main/java/com/library/controller"

for filename in os.listdir(dir_path):
    if not filename.endswith("Servlet.java"):
        continue
    filepath = os.path.join(dir_path, filename)
    with open(filepath, "r") as f:
        content = f.read()

    # We need to replace JsonResponse.send(...) 
    # Usually they look like JsonResponse.send(resp, 200, "...", data);
    # or JsonResponse.send(resp, 400, "...", null);
    # Let's match JsonResponse.send(resp, <code>, <msg>, <data>)
    
    def replacer(match):
        resp, code, msg, data = match.group(1), match.group(2), match.group(3), match.group(4)
        
        # Determine which method to use based on code and data
        try:
            int_code = int(code.strip())
        except ValueError:
            int_code = 200
        
        data_is_null = data.strip() == "null"
        
        if int_code >= 400:
            return f"JsonResponse.sendError({resp}, {code}, {msg})"
        else:
            if data_is_null:
                return f"JsonResponse.sendMessage({resp}, {code}, {msg})"
            else:
                return f"JsonResponse.sendSuccess({resp}, {code}, {data}, {msg})"

    # Regex to capture arguments of JsonResponse.send(resp, code, msg, data)
    # The arguments might be separated by comma. Msg might have commas inside it, but we can assume it's simple enough or just use a generic parser.
    # Actually, java strings can have commas. Let's write a simple parser.
    
    new_content = ""
    idx = 0
    while True:
        pos = content.find("JsonResponse.send(", idx)
        if pos == -1:
            new_content += content[idx:]
            break
        new_content += content[idx:pos]
        
        # find the matching closing parenthesis
        open_parens = 1
        i = pos + 18
        while i < len(content) and open_parens > 0:
            if content[i] == '(': open_parens += 1
            elif content[i] == ')': open_parens -= 1
            i += 1
            
        args_str = content[pos+18:i-1]
        # split by commas, but ignore commas inside quotes
        args = []
        current_arg = ""
        in_quotes = False
        for c in args_str:
            if c == '"': in_quotes = not in_quotes
            if c == ',' and not in_quotes:
                args.append(current_arg)
                current_arg = ""
            else:
                current_arg += c
        args.append(current_arg)
        
        if len(args) >= 4:
            resp, code, msg, data = args[0], args[1], args[2], args[3]
            data = ", ".join(args[3:])
            
            # evaluate
            try:
                int_code = int(code.strip())
            except ValueError:
                int_code = 200
                
            data_is_null = data.strip() == "null"
            
            if int_code >= 400:
                replacement = f"JsonResponse.sendError({resp}, {code}, {msg})"
            else:
                if data_is_null:
                    replacement = f"JsonResponse.sendMessage({resp}, {code}, {msg})"
                else:
                    replacement = f"JsonResponse.sendSuccess({resp}, {code}, {data}, {msg})"
            
            new_content += replacement
        elif len(args) == 3: # In some cases it might be send(resp, code, msg)
            resp, code, msg = args[0], args[1], args[2]
            try:
                int_code = int(code.strip())
            except ValueError:
                int_code = 200
            if int_code >= 400:
                new_content += f"JsonResponse.sendError({resp}, {code}, {msg})"
            else:
                new_content += f"JsonResponse.sendMessage({resp}, {code}, {msg})"
        else:
            new_content += f"JsonResponse.send({args_str})"
            
        idx = i

    with open(filepath, "w") as f:
        f.write(new_content)

