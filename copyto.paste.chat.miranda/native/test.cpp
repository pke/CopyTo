#define WIN32_LEAN_AND_MEAN
#define UNICODE
#include <windows.h>
#include <tchar.h>
#include <TlHelp32.h>
#include <sys/types.h>

typedef struct COMMAND_INFO_TYPE
{
	char*  lpCommand;
	struct COMMAND_INFO_TYPE *last, *next;
}
	COMMAND_INFO;

typedef struct
{
	LOGFONT  lf;
	COLORREF color;
}
	FONTINFO;

typedef struct LOG_INFO_TYPE
{
	TCHAR*  ptszText;
	TCHAR*  ptszNick;
	TCHAR*  ptszUID;
	TCHAR*  ptszStatus;
	TCHAR*  ptszUserInfo;
	BOOL    bIsMe;
	BOOL    bIsHighlighted;
	time_t  time;
	int     iType;
	struct  LOG_INFO_TYPE *next;
	struct  LOG_INFO_TYPE *prev;
}
	LOGINFO;

typedef struct STATUSINFO_TYPE
{
	TCHAR*  pszGroup;
	HICON   hIcon;
	WORD    Status;
	struct  STATUSINFO_TYPE *next;
}
	STATUSINFO;
typedef struct  USERINFO_TYPE
{
	TCHAR* pszNick;
	TCHAR* pszUID;
	WORD   Status;
	int    iStatusEx;
	WORD   ContactStatus;
	struct USERINFO_TYPE *next;
}
	USERINFO;

typedef struct  TABLIST_TYPE
{
	TCHAR* pszID;
	char*  pszModule;
	struct TABLIST_TYPE *next;
}
	TABLIST;

typedef struct SESSION_INFO_TYPE
{
	HWND        hWnd;

	BOOL        bFGSet;
	BOOL        bBGSet;
	BOOL        bFilterEnabled;
	BOOL        bNicklistEnabled;
	BOOL        bInitDone;

	char*       pszModule;
	TCHAR*      ptszID;
	TCHAR*      ptszName;
	TCHAR*      ptszStatusbarText;
	TCHAR*      ptszTopic;

	// I hate m3x, Unicode, IRC, chats etc...
	#if !defined( _UNICODE )
		char*    pszID;		// ugly fix for returning static ANSI strings in GC_INFO
		char*    pszName;   // just to fix a bug quickly, should die after porting IRC to Unicode
	#endif

	int         iType;
	int         iFG;
	int         iBG;
	int         iSplitterY;
	int         iSplitterX;
	int         iLogFilterFlags;
	int         nUsersInNicklist;
	int         iEventCount;
	int         iX;
	int         iY;
	int         iWidth;
	int         iHeight;
	int         iStatusCount;

	WORD        wStatus;
	WORD        wState;
	WORD        wCommandsNum;
	DWORD       dwItemData;
	DWORD       dwFlags;
	HANDLE      hContact;
	HWND        hwndStatus;
	time_t      LastTime;

	COMMAND_INFO*  lpCommands;
	COMMAND_INFO*  lpCurrentCommand;
	LOGINFO*       pLog;
	LOGINFO*       pLogEnd;
	USERINFO*      pUsers;
	USERINFO*      pMe;
	STATUSINFO*    pStatuses;

	HWND        hwndTooltip;
	int         iOldItemID;

	struct SESSION_INFO_TYPE *next;
}
	SESSION_INFO;

void readProcessString(HANDLE process, void* startAddress, TCHAR* text, int textLen) {
    TCHAR* pText = text;
    DWORD read;
    BYTE* offset = (BYTE*)startAddress;
    while (pText < text+ textLen - 1) {
        if (!ReadProcessMemory(process, offset, pText, sizeof(TCHAR), &read) || read != sizeof(TCHAR)) {
            *pText = 0;
        }
        if (*pText == 0) {
            break;
        }
        ++pText;
        offset += sizeof(TCHAR);
    }
}

BOOL WINAPI EnumProc(HWND window, LPARAM) {
    TCHAR text[1024];
    GetWindowText(window, text, 1024);
    OutputDebugString(text);
    OutputDebugStringA("\n");
    if (text[0] == _T('#')) {
        DWORD processId;
        GetWindowThreadProcessId(window, &processId);
        HANDLE process = OpenProcess(PROCESS_VM_READ | PROCESS_QUERY_INFORMATION, FALSE, processId);
        void* addr = (void*)GetWindowLong(window, GWL_USERDATA);
        SESSION_INFO info;
        DWORD read;
        ReadProcessMemory(process, addr, &info, sizeof(info), &read);
        if (IsWindow(info.hWnd)) {
            // Get User List
            USERINFO user;
            ReadProcessMemory(process, info.pUsers, &user, sizeof(user), &read);
            do {               TH32CS_INHERIT 
                TCHAR userName[1024];
                readProcessString(process, (BYTE*)user.pszNick, userName, 1024);
                OutputDebugStringA("User: ");
                OutputDebugString(userName);
                OutputDebugStringA("\n");
                if (user.next) {
                    ReadProcessMemory(process, user.next, &user, sizeof(user), &read);
                } else {
                    break;
                }
            } while (true);
            return FALSE;
        }
    }
    return TRUE;
}

int main(int argc, char* argv[]) {

    EnumWindows(EnumProc, 0);

	return 0;
}
