import {Component} from '@angular/core';
import {Router} from '@angular/router';
import {ChoreService} from '../../../services/chore.service';
import {ToastrService} from 'ngx-toastr';
import {UserDetail} from '../../../dtos/auth-request';

@Component({
  selector: 'app-leaderboard',
  templateUrl: './leaderboard.component.html',
  styleUrls: ['./leaderboard.component.scss'],
})
export class LeaderboardComponent {
  users: UserDetail[];
  groupedUsers: { points: number; users: UserDetail[] }[];
  totalPoints: number = 0;

  constructor(
    private router: Router,
    private choreService: ChoreService
  ) {
  }

  ngOnInit() {
    this.choreService.getUsers().subscribe({
      next: (res) => {
        this.groupedUsers = this.groupUsersByPoints(res);

        for (let i = 0; i < this.groupedUsers.length; i++) {
          this.totalPoints += this.groupedUsers[i]?.points != null ? this.groupedUsers[i]?.points : 0;
        }

        this.groupedUsers = this.groupedUsers.sort((a, b) => b.points - a.points);

        this.users = this.groupedUsers.reduce((acc, group) => acc.concat(group.users), []);
      }
    });
  }

  getRowNumberStyle(rank: number): any {
    if (rank === 1) {
      return {color: 'gold'};
    } else if (rank === 2) {
      return {color: 'silver'};
    } else if (rank === 3) {
      return {color: '#cd7f32'};
    } else {
      return {color: 'grey'};
    }
  }

  private groupUsersByPoints(users: UserDetail[]): { points: number; users: UserDetail[] }[] {
    const userGroupsMap = new Map<number, UserDetail[]>();

    users.forEach((user) => {
      const points = user.points;
      if (!userGroupsMap.has(points)) {
        userGroupsMap.set(points, []);
      }
      userGroupsMap.get(points).push(user);
    });

    return Array.from(userGroupsMap.entries()).map(([points, users]) => ({points, users}));
  }

  navigateToAllChores() {
    this.router.navigate(['chores', 'all']);
  }

  navigateToMyChores() {
    this.router.navigate(['chores', 'my']);

  }

  navigateToPreference() {
    this.router.navigate(['chores', 'preference']);

  }
}
